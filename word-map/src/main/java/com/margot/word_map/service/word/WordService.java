package com.margot.word_map.service.word;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.margot.word_map.dto.request.CreateWordRequest;
import com.margot.word_map.dto.request.DictionaryListRequest;
import com.margot.word_map.dto.request.SymbolPosition;
import com.margot.word_map.dto.request.UpdateWordRequest;
import com.margot.word_map.dto.response.DictionaryListResponse;
import com.margot.word_map.dto.response.DictionaryWordResponse;
import com.margot.word_map.exception.LanguageNotFoundException;
import com.margot.word_map.exception.WordAlreadyExists;
import com.margot.word_map.exception.WordNotFoundException;
import com.margot.word_map.mapper.WordMapper;
import com.margot.word_map.model.Admin;
import com.margot.word_map.model.Language;
import com.margot.word_map.model.Word;
import com.margot.word_map.repository.WordRepository;
import com.margot.word_map.service.language.LanguageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WordService {

    private final LanguageService languageService;
    private final WordRepository wordRepository;
    private final WordMapper wordMapper;

    public DictionaryWordResponse getWordInfo(String word) {
        return wordMapper.toDictionaryWordResponse(wordRepository.findWordByWord(word).orElseThrow(() -> {
            log.info("word not found {}", word);
            return new WordNotFoundException("word " + word + " not found");
        }));
    }

    public void createNewWord(UserDetails userDetails, CreateWordRequest request) {
        Admin admin = (Admin) userDetails;
        Language language = languageService.findById(request.getLanguageId())
                .orElseThrow(() -> new NoSuchElementException("Нет языка с таким id"));

        wordRepository.findWordByWord(request.getWord()).ifPresentOrElse(
                (word) -> {
                    log.info("word {} already exists", request.getWord());
                    throw new WordAlreadyExists("word " + request.getWord() + " already exists");
                },
                () -> {
                    Word word = Word.builder()
                            .word(request.getWord())
                            .description(request.getDescription())
                            .wordLength(request.getWord().length())
                            .dateCreation(LocalDateTime.now())
                            .editedBy(admin)
                            .language(language)
                            .createdBy(admin)
                            .build();

                    wordRepository.save(word);
                    log.info("CREATE WORD Пользователь {} добавил новое слово {}", admin.getEmail(), request.getWord());
                }
        );
    }

    @Transactional
    public void updateWordInfo(UserDetails userDetails, UpdateWordRequest request) {
        Admin admin = (Admin) userDetails;

        Optional<Word> wordByNameOp = wordRepository.findWordByWord(request.getWord());
        if (wordByNameOp.isPresent() && !wordByNameOp.get().getId().equals(request.getId())) {
            log.info("word {} already exists", request.getWord());
            throw new WordAlreadyExists("word " + request.getWord() + " already exists with another id");
        }

        Word wordToUpdate = wordByNameOp.orElseGet(() -> {
            Optional<Word> wordById = wordRepository.findById(request.getId());
            return wordById.orElseThrow(() -> {
                log.info("word with id {} not found", request.getId());
                return new WordNotFoundException("word with id " + request.getId() + " not found");
            });
        });

        wordToUpdate.setWord(request.getWord());
        wordToUpdate.setDescription(request.getDescription());
        wordToUpdate.setWordLength(request.getWord().length());
        wordToUpdate.setDateEdited(LocalDateTime.now());
        wordToUpdate.setEditedBy(admin);
        wordRepository.save(wordToUpdate);
    }

    @Transactional
    public void deleteWord(UserDetails userDetails, Long id) {
        Admin admin = (Admin) userDetails;

        Word word = wordRepository.findById(id)
                .orElseThrow(() -> new WordNotFoundException("Word with id " + id + " not found"));

        wordRepository.delete(word);
        log.info("DELETE WORD Пользователь {} удалил слово {}.", admin.getId(), word.getWord());
    }

    @Transactional(readOnly = true)
    public StreamingResponseBody getAllWords() {
        return outputStream -> {
            try (JsonGenerator generator = new ObjectMapper().getFactory().createGenerator(outputStream)) {
                generator.writeStartArray();

                int page = 0;
                int pageSize = 1000;

                Page<Word> wordPage;
                do {
                    wordPage = wordRepository.findAll(PageRequest.of(page, pageSize));

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
        Language language = languageService.findByName(request.getLanguage())
                .orElseThrow(() -> new LanguageNotFoundException("Нет такого языка"));
        int wordLength = request.getWordLength();
        boolean reuse = request.getReuse();
        String lettersUsed = request.getLettersUsed();
        String lettersExclude = request.getLettersExclude();
        List<SymbolPosition> positions = request.getPositions();

        String regex = ".*[" + lettersExclude + "].*";
        List<String> words = wordRepository.findWordsByLanguageNotMatchingRegex(language.getId(), regex);

        if (wordLength != 0) {
            words = words.stream().filter(w -> w.length() == wordLength).collect(Collectors.toList());
        }

        words = filterByLettersOnPositions(positions, words);

        if (lettersUsed != null && !lettersUsed.isEmpty()) {
            words = filterByLetters(lettersUsed, words);
            if (!reuse) {
                words = filterByUniqueLetters(lettersUsed, words);
            }
        }
        return DictionaryListResponse.builder()
                .word(words)
                .build();
    }

    private static List<String> filterByLetters(String lettersUsed, List<String> words) {
        Set<Character> requiredLetters = lettersUsed.chars()
                .mapToObj(c -> (char) c)
                .collect(Collectors.toSet());

        words = words.stream()
                .filter(word -> word.chars()
                        .mapToObj(c -> (char) c)
                        .allMatch(requiredLetters::contains))
                .collect(Collectors.toList());
        return words;
    }

    private static List<String> filterByUniqueLetters(String lettersUsed, List<String> words) {
        Map<Character, Long> usedLetterCount = lettersUsed.chars()
                .mapToObj(c -> (char) c)
                .collect(Collectors.groupingBy(c -> c, Collectors.counting()));

        words = words.stream()
                .filter(word -> {
                    Map<Character, Long> wordCount = word.chars()
                            .mapToObj(c -> (char) c)
                            .collect(Collectors.groupingBy(c -> c, Collectors.counting()));
                    return usedLetterCount.entrySet().stream()
                            .allMatch(e -> wordCount.getOrDefault(e.getKey(), 0L) <= e.getValue());
                })
                .collect(Collectors.toList());
        return words;
    }

    private List<String> filterByLettersOnPositions(List<SymbolPosition> positions, List<String> words) {
        if (positions != null && !positions.isEmpty()) {
            for (SymbolPosition pos : positions) {
                int index = pos.getNumber();
                char expectedChar = pos.getLetter();
                words = words.stream()
                        .filter(word -> word.length() > index && word.charAt(index) == expectedChar)
                        .collect(Collectors.toList());
            }
        }
        return words;
    }
}