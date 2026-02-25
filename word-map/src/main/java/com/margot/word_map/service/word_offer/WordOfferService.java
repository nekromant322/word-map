package com.margot.word_map.service.word_offer;

import com.margot.word_map.dto.request.CreateWordRequest;
import com.margot.word_map.dto.request.WordOfferChangeStatus;
import com.margot.word_map.dto.response.WordOfferResponse;
import com.margot.word_map.exception.WordAlreadyExists;
import com.margot.word_map.exception.WordNotFoundException;
import com.margot.word_map.mapper.WordOfferMapper;
import com.margot.word_map.model.Admin;
import com.margot.word_map.model.WordOffer;
import com.margot.word_map.model.WordOfferStatus;
import com.margot.word_map.repository.WordOfferRepository;
import com.margot.word_map.repository.WordRepository;
import com.margot.word_map.service.word.WordService;
import com.margot.word_map.utils.security.SecurityAdminAccessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class WordOfferService {

    private final WordOfferRepository wordOfferRepository;
    private final WordService wordService;
    private final WordRepository wordRepository;
    private final WordOfferMapper wordOfferMapper;
    private final SecurityAdminAccessor adminAccessor;

    @Transactional
    public void save(WordOffer wordOffer) {
        wordOfferRepository.save(wordOffer);
    }

    public void isAlreadyExist(CreateWordRequest wordRequest) {
        if (findByWordInTableWords(wordRequest.getWord()) ||
                findByWordInTableWordsOffer(wordRequest.getWord())) {
            throw new WordAlreadyExists("word " + wordRequest.getWord() + " already exists");
        }
    }

    // TODO будет реализован после добавления игроков в рамках WM-173
    @Transactional
    public void processWordOffer(CreateWordRequest request) {

    }

    public boolean findByWordInTableWords(String word) {
        return wordRepository.findWordByWord(word).isPresent();
    }

    public boolean findByWordInTableWordsOffer(String word) {
        return wordOfferRepository.findByWord(word).isPresent();
    }

    public Page<WordOfferResponse> getOffers(WordOfferStatus statusFilter, int page, int size,
                                             String sortBy, String sortDir) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.fromString(sortDir), sortBy)
        );

        Specification<WordOffer> spec = Specification.where(null);

        if (statusFilter != null) {
            try {
                spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), statusFilter));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Неверный статус: " + statusFilter);
            }
        }

        return wordOfferRepository.findAll(spec, pageable).map(wordOfferMapper::toResponse);
    }

    //Todo Потом добавим еще и юзера, чтобы считать сколько слов добавил и рейтинг
    @Transactional
    public void approve(Long id, String description) {
        Admin admin = adminAccessor.getCurrentAdmin();

        Optional<WordOffer> wordOfferOptional = wordOfferRepository.findById(id);
        if (wordOfferOptional.isEmpty()) {
            throw new WordNotFoundException("word offer with " + id + " not found");
        }
        WordOffer wordOffer = wordOfferOptional.get();

        wordService.createNewWord(new CreateWordRequest(
                wordOffer.getWord(), description, wordOffer.getLanguageId()));

        wordOffer.setStatus(WordOfferStatus.APPROVED);
        wordOfferRepository.save(wordOffer);
        log.info("Администратор{} одобрил предложение слова {}", admin.getEmail(), wordOffer.getWord());
    }

    @Transactional
    public void changeStatus(WordOfferChangeStatus status) {
        WordOffer wordOffer = wordOfferRepository.findById(status.getId()).orElseThrow(() ->
                new  WordNotFoundException("Нет слова с таким id в предложке"));
        wordOffer.setStatus(WordOfferStatus.valueOf(status.getStatus()));
        wordOfferRepository.save(wordOffer);
        log.info("Статус слова с id {} изменен на {}", status.getId(), status.getStatus());
    }

    @Transactional
    public void reject(Long id) {
        Admin admin = adminAccessor.getCurrentAdmin();

        Optional<WordOffer> wordOfferOptional = wordOfferRepository.findById(id);
        if (wordOfferOptional.isEmpty()) {
            throw new WordNotFoundException("word offer with " + id + " not found");
        }
        WordOffer wordOffer = wordOfferOptional.get();
        wordOffer.setStatus(WordOfferStatus.REJECTED);
        wordOfferRepository.save(wordOffer);
        log.info("Администратор {} отклонил предложение слова {}", admin.getEmail(), wordOffer.getWord());
    }
}
