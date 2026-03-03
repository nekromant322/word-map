package com.margot.word_map.service.word;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.margot.word_map.dto.PageDto;
import com.margot.word_map.dto.WordOfferPage;
import com.margot.word_map.dto.request.*;
import com.margot.word_map.dto.response.*;
import com.margot.word_map.exception.*;
import com.margot.word_map.mapper.WordMapper;
import com.margot.word_map.model.*;
import com.margot.word_map.repository.WordOfferRepository;
import com.margot.word_map.repository.WordRepository;
import com.margot.word_map.repository.specification.WordOfferSpecification;
import com.margot.word_map.service.audit.AuditActionType;
import com.margot.word_map.service.audit.AuditService;
import com.margot.word_map.service.language.LanguageService;
import com.margot.word_map.service.map.LetterService;
import com.margot.word_map.utils.security.SecurityAdminAccessor;
import com.margot.word_map.utils.security.SecurityPlayerAccessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WordService {

    private final LanguageService languageService;
    private final WordRepository wordRepository;
    private final WordMapper wordMapper;
    private final SecurityAdminAccessor adminAccessor;
    private final AuditService auditService;
    private final LetterService letterService;
    private final WordOfferRepository wordOfferRepository;
    private final WordSpecs wordSpecs;
    private final SecurityPlayerAccessor playerAccessor;
    private final WordOfferSpecification wordOfferSpecification;

    @Transactional(readOnly = true)
    public DictionaryDetailedWordResponse getWordByLanguageId(Long languageId, String word) {
        return wordMapper.toDictionaryDetailedWordResponse(wordRepository.findWordByWordAndLanguageId(word, languageId)
                .orElseThrow(() -> new WordOfferAlreadyExistsException("Слово не найдено")));
    }

    @PreAuthorize("hasPermission(null, 'MANAGE_DICTIONARY')")
    @Transactional
    public void createNewWord(CreateWordRequest request) {
        Admin admin = adminAccessor.getCurrentAdmin();
        Language language = languageService.findById(request.getLanguageId())
                .orElseThrow(() -> new NoSuchElementException("Нет языка с таким id"));
        adminAccessor.checkLanguageAccess(language);

        Set<Character> alphabet = letterService.getAllowedLetters(request.getLanguageId());
        if (!letterService.validateAlphabet(request.getWord(), alphabet)) {
            throw new FormatErrorException();
        }

        wordRepository.findWordByWord(request.getWord()).ifPresentOrElse(
                (word) -> {
                    throw new WordAlreadyExists("Слово " + request.getWord() + " уже существует");
                },
                () -> {
                    Word word = Word.builder()
                            .word(request.getWord())
                            .description(request.getDescription())
                            .wordLength(request.getWord().length())
                            .createdAt(LocalDateTime.now())
                            .editedBy(admin)
                            .language(language)
                            .createdBy(admin)
                            .build();

                    wordRepository.save(word);
                    WordOfferStatus status = WordOfferStatus.APPROVED;
                    wordOfferRepository.updateStatus(request.getWord(), request.getLanguageId(), status);
                    auditService.log(AuditActionType.DICTIONARY_WORD_ADDED, request.getWord());
                }
        );
    }

    @PreAuthorize("hasPermission(null, 'MANAGE_DICTIONARY')")
    @Transactional
    public void updateWordInfo(UpdateWordRequest request, Long wordId) {
        Admin admin = adminAccessor.getCurrentAdmin();

        Word wordToUpdate = wordRepository.findById(wordId).orElseThrow(() ->
                new WordOfferAlreadyExistsException("Слово с id " + wordId + " не найдено"));

        adminAccessor.checkLanguageAccess(wordToUpdate.getLanguage());

        wordToUpdate.setDescription(request.getDescription());
        wordToUpdate.setEditedAt(LocalDateTime.now());
        wordToUpdate.setEditedBy(admin);
        wordRepository.save(wordToUpdate);
        auditService.log(AuditActionType.DICTIONARY_WORD_UPDATED, wordToUpdate.getWord());
    }

    @PreAuthorize("hasPermission(null, 'MANAGE_DICTIONARY')")
    @Transactional
    public void deleteWord(Long id) {
        Admin admin = adminAccessor.getCurrentAdmin();

        Word word = wordRepository.findById(id)
                .orElseThrow(() -> new WordOfferAlreadyExistsException("Слово не найдено"));

        adminAccessor.checkLanguageAccess(word.getLanguage());

        wordRepository.delete(word);
        log.info("Пользователь {} удалил слово {}.", admin.getId(), word.getWord());
        auditService.log(AuditActionType.DICTIONARY_WORD_DELETED, word.getWord());
    }

    @Transactional(readOnly = true)
    public StreamingResponseBody getAllWordsByLanguageId(Long languageId) {
        return outputStream -> {
            try (JsonGenerator generator = new ObjectMapper().getFactory().createGenerator(outputStream)) {
                generator.writeStartArray();

                int page = 0;
                int pageSize = 1000;

                Page<Word> wordPage;
                do {
                    wordPage = wordRepository.findAllByLanguageId(languageId, PageRequest.of(page, pageSize));

                    for (Word word : wordPage) {
                        generator.writeObject(new DictionaryWordResponse(
                                word.getId(),
                                word.getWord(),
                                word.getDescription()
                        ));
                    }
                    page++;
                } while (!wordPage.isLast());
                generator.writeEndArray();
            }
        };
    }

    @Transactional(readOnly = true)
    public DictionaryListResponse getWordsByFilters(DictionaryListRequest request) {
        languageService.findById(request.getLanguageId())
                .orElseThrow(() -> new FormatErrorException("Нет такого языка"));
        boolean reuse = request.getReuse();
        List<String> words;
        if (!reuse) {
            words = filterByUniqueLetters(request.getLettersUsed(), request.getLanguageId());
        } else {
            words = filterByLetters(request);
        }
        return DictionaryListResponse.builder()
                .word(words)
                .build();
    }

    private List<String> filterByLetters(DictionaryListRequest request) {
        validateRequest(request);
        Specification<Word> specification = Specification
                .where(wordSpecs.hasLanguageId(request))
                .and(wordSpecs.hasWordLength(request))
                .and(wordSpecs.matchingLettersUsed(request))
                .and(wordSpecs.lettersExcluded(request))
                .and(wordSpecs.hasPositions(request));
        return wordRepository
                .findAll(specification)
                .stream()
                .map(Word::getWord)
                .collect(Collectors.toList());
    }

    private List<String> filterByUniqueLetters(String lettersUsed, Long languageId) {
        String lowerCaseLetter = lettersUsed.toLowerCase();

        Set<Character> alphabet = letterService.getAllowedLetters(languageId);
        if (!letterService.validateAlphabet(lettersUsed, alphabet)) {
            throw new FormatErrorException();
        }

        Map<Character, Long> usedLetterCount = lowerCaseLetter.chars()
                .mapToObj(c -> (char) c)
                .collect(Collectors.groupingBy(c -> c, Collectors.counting()));
        String regex = "[" + lowerCaseLetter + "]";
        List<String> words = wordRepository.findWordsByLetters(languageId, regex, lowerCaseLetter);

        words = words.stream()
                .filter(word -> {
                    Map<Character, Long> wordCount = word.toLowerCase().chars()
                            .mapToObj(c -> (char) c)
                            .collect(Collectors.groupingBy(c -> c, Collectors.counting()));
                    return usedLetterCount.entrySet().stream()
                            .allMatch(e -> Objects
                                    .equals(wordCount.getOrDefault(e.getKey(), 0L), e.getValue()));
                })
                .collect(Collectors.toList());
        return words;
    }

    private void validateRequest(DictionaryListRequest request) {
        Set<Character> alphabet = letterService.getAllowedLetters(request.getLanguageId());
        if (request.getLettersUsed() != null) {
            if (!letterService.validateAlphabet(request.getLettersUsed(), alphabet)) {
                throw new FormatErrorException("'lettersUsed' содержит некорректные символы");
            }
        }

        if (request.getLettersExclude() != null) {
            if (!letterService.validateAlphabet(request.getLettersExclude(), alphabet)) {
                throw new FormatErrorException("'lettersExclude' содержит некорректные символы");
            }
        }
        if (request.getPositions() != null) {
            for (SymbolPosition pos : request.getPositions()) {
                if (!letterService.validateAlphabet(pos.getLetter().toString(), alphabet)) {
                    throw new FormatErrorException("'letter' содержит некорректные символы");
                }
            }
        }
    }

    @Transactional
    public OfferResponse processWordOffer(CreateWordOfferRequest request) {
        isAlreadyExist(request);

        List<WordOfferStatus> statuses = wordOfferRepository.
                findDistinctStatusByWordAndLanguageId(request.getWord(), request.getLanguageId());
        WordOfferStatus status = getWordOfferStatus(statuses);
        wordOfferRepository.updateStatus(request.getWord(), request.getLanguageId(), status);
        Player player = playerAccessor.getCurrentPlayer();

        WordOffer offer = WordOffer.builder()
                .word(request.getWord().toLowerCase())
                .languageId(player.getLanguage().getId())
                .playerId(player.getId())
                .status(status)
                .createdAt(LocalDateTime.now())
                .build();
        wordOfferRepository.save(offer);

        return OfferResponse.builder()
                .id(offer.getId())
                .word(offer.getWord())
                .build();
    }

    private static WordOfferStatus getWordOfferStatus(List<WordOfferStatus> statuses) {
        if (statuses == null || statuses.isEmpty()) {
            return WordOfferStatus.CHECK;
        } else if (statuses.size() == 1) {
            WordOfferStatus found = statuses.getFirst();
            return (found == WordOfferStatus.APPROVED) ? WordOfferStatus.CHECK : found;
        }
        return statuses.contains(WordOfferStatus.REJECTED)
                ? WordOfferStatus.REJECTED
                : WordOfferStatus.CHECK;
    }

    private void isAlreadyExist(CreateWordOfferRequest wordRequest) {
        if (findByWordInTableWords(wordRequest.getWord(), wordRequest.getLanguageId())) {
            throw new WordAlreadyExists("Слово " + wordRequest.getWord() + " уже существует");
        }
        if (findByWordInTableWordsOffer(wordRequest.getWord(), playerAccessor.getCurrentPlayerId())) {
            throw new WordOfferAlreadyExistsException("Слово уже на рассмотрении " + wordRequest.getWord());
        }
    }

    private boolean findByWordInTableWords(String word, Long languageId) {
        return wordRepository.findWordByWordAndLanguageId(word, languageId).isPresent();
    }

    private boolean findByWordInTableWordsOffer(String word, Long playerId) {
        return wordOfferRepository.findOfferByWordAndPlayerId(word, playerId).isPresent();
    }

    @Transactional
    @PreAuthorize("hasPermission(null, 'MANAGE_DICTIONARY')")
    public void changeStatus(WordOfferChangeStatus request) {
        if (request.getStatus() == WordOfferStatus.APPROVED) {
            if (wordRepository.findWordByWordAndLanguageId(request.getWord(), request.getLanguageId()).isEmpty()) {
                throw new InvalidConditionException("Запись не найдена");
            }
        }
        wordOfferRepository.updateStatus(request.getWord(), request.getLanguageId(), request.getStatus());
        auditService.log(AuditActionType.DICTIONARY_OFFER_STATUS_CHANGED, request.getWord());
    }

    @Transactional
    public List<OfferListResponse> getAllPlayerOffers() {
        Player player = playerAccessor.getCurrentPlayer();

        return wordOfferRepository
                .findOfferedWordByPlayer(player.getId(), player.getLanguage().getId())
                .stream()
                .map(offer -> OfferListResponse.builder()
                        .id(offer.getId())
                        .word(offer.getWord())
                        .createdAt(offer.getCreatedAt())
                        .status(offer.getStatus())
                        .build())
                .toList();
    }

    @Transactional
    @PreAuthorize("hasPermission(null, 'MANAGE_DICTIONARY')")
    public WordOfferAdminResponse getAllAdminOffers(WordOfferAdminRequest request, Pageable pageable) {
        Set<Character> alphabet = letterService.getAllowedLetters(request.getLanguageId());
        languageService.findById(request.getLanguageId())
                .orElseThrow(() -> new FormatErrorException("Нет такого языка"));

        if (request.getSearch() != null &&
                !letterService.validateAlphabet(request.getSearch(), alphabet)) {
            throw new FormatErrorException("'search' содержит некорректные символы");
        }

        Page<WordOfferPage> page =
                wordOfferSpecification.findAll(
                        request.getLanguageId(),
                        request.getSearch(),
                        request.getFilterStatus(),
                        request.getSortingType(),
                        pageable);

        int numberPage = pageable.getPageNumber() + 1;
        int pageSize = pageable.getPageSize();
        if (numberPage > page.getTotalPages() && page.getTotalElements() > 0) {
            throw new PageOutOfRangeException("Запрошенная страница не существует");
        }
        return WordOfferAdminResponse.builder()
                .content(page.getContent())
                .pageable(PageDto.builder()
                        .numberPage(numberPage)
                        .pageSize(pageSize)
                        .build())
                .totalPages(page.getTotalPages())
                .totalElements((int) page.getTotalElements())
                .build();
    }
}