package com.margot.word_map.service.word_offer;

import com.margot.word_map.dto.request.CreateWordRequest;
import com.margot.word_map.dto.response.WordOfferResponse;
import com.margot.word_map.exception.WordAlreadyExists;
import com.margot.word_map.exception.WordNotFoundException;
import com.margot.word_map.mapper.WordOfferMapper;
import com.margot.word_map.model.Admin;
import com.margot.word_map.model.User;
import com.margot.word_map.model.WordOffer;
import com.margot.word_map.repository.WordOfferRepository;
import com.margot.word_map.repository.WordRepository;
import com.margot.word_map.service.word.WordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

    @Transactional
    public void processWordOffer(CreateWordRequest request, UserDetails userDetails) {
        User user = (User) userDetails;
        isAlreadyExist(request);

        WordOffer wordOffer = WordOffer.builder()
                .word(request.getWord())
                .userId(user.getId())
                .createdAt(LocalDateTime.now())
                .build();

        wordOfferRepository.save(wordOffer);
    }

    public boolean findByWordInTableWords(String word) {
        return wordRepository.findWordByWord(word).isPresent();
    }

    public boolean findByWordInTableWordsOffer(String word) {
        return wordOfferRepository.findByWord(word).isPresent();
    }

    public Page<WordOfferResponse> getOffers(String statusFilter, int page, int size, String sortBy, String sortDir) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.fromString(sortDir), sortBy)
        );

        Specification<WordOffer> spec = Specification.where(null);

        if (statusFilter != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("status"), WordOffer.STATUS.valueOf(statusFilter.toUpperCase()))
            );
        }
        return wordOfferRepository.findAll(spec, pageable).map(wordOfferMapper::toResponse);
    }

    //Todo Потом добавим еще и юзера, чтобы считать сколько слов добавил и рейтинг
    @Transactional
    public void approve(UserDetails userDetails, Long id, String description) {
        Optional<WordOffer> wordOfferOptional = wordOfferRepository.findById(id);
        if (wordOfferOptional.isEmpty()) {
            throw new WordNotFoundException("word offer with " + id + " not found");
        }
        WordOffer wordOffer = wordOfferOptional.get();

        wordService.createNewWord(userDetails, wordOffer.getWord(), description);

        wordOffer.setStatus(WordOffer.STATUS.APPROVED);
        wordOfferRepository.save(wordOffer);
    }

    @Transactional
    public void reject(UserDetails userDetails, Long id) {
        Admin admin = (Admin) userDetails;

        Optional<WordOffer> wordOfferOptional = wordOfferRepository.findById(id);
        if (wordOfferOptional.isEmpty()) {
            throw new WordNotFoundException("word offer with " + id + " not found");
        }
        WordOffer wordOffer = wordOfferOptional.get();
        wordOffer.setStatus(WordOffer.STATUS.REJECTED);
        wordOfferRepository.save(wordOffer);
        log.info("REJECT WORD Пользователь {} не добавил новое слово {}", admin.getEmail(), wordOffer.getWord());
    }
}
