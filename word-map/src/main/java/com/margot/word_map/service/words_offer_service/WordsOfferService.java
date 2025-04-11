package com.margot.word_map.service.words_offer_service;

import com.margot.word_map.dto.request.CreateWordRequest;
import com.margot.word_map.dto.response.WordOfferResponse;
import com.margot.word_map.exception.WordAlreadyExists;
import com.margot.word_map.exception.WordNotFoundException;
import com.margot.word_map.mapper.WordOfferMapper;
import com.margot.word_map.model.Admin;
import com.margot.word_map.model.WordOffer;
import com.margot.word_map.repository.WordRepository;
import com.margot.word_map.repository.WordsOfferRepository;
import com.margot.word_map.service.WordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class WordsOfferService {

    private final WordsOfferRepository wordsOfferRepository;
    private final WordService wordService;
    private final WordRepository wordRepository;
    private final WordOfferMapper wordOfferMapper;

    @Transactional
    public void save(WordOffer wordOffer) {
        wordsOfferRepository.save(wordOffer);
    }

    public void isAlreadyExist(CreateWordRequest wordRequest) {
        if (findByWordInTableWords(wordRequest.getWord()) ||
                findByWordInTableWordsOffer(wordRequest.getWord())) {
            throw new WordAlreadyExists("word " + wordRequest.getWord() + " already exists");
        }
    }

    //Изменим админа на юзера
    @Transactional
    public void processWordOffer(CreateWordRequest request, UserDetails userDetails) {
        Admin user = (Admin) userDetails;
        isAlreadyExist(request);

        WordOffer wordOffer = WordOffer.builder()
                .word(request.getWord())
                .description(request.getDescription())
                .userId(user.getId())
                .build();

        wordsOfferRepository.save(wordOffer);
    }

    public boolean findByWordInTableWords(String word) {
        return wordRepository.findWordByWord(word).isPresent();
    }

    public boolean findByWordInTableWordsOffer(String word) {
        return wordsOfferRepository.findByWord(word).isPresent();
    }

    public Page<WordOfferResponse> getAllWordsOffersNotChecked(Pageable pageable) {
        return wordsOfferRepository.findAllByCheckedIsFalse(pageable)
                .map(wordOfferMapper::toResponse);
    }

    //Потом добавим еще и юзера, чтобы считать сколько слов добавил и рейтинг
    @Transactional
    public void approve(UserDetails userDetails, Long id) {
        Optional<WordOffer> wordOfferOptional = wordsOfferRepository.findById(id);
        if (wordOfferOptional.isEmpty()) {
            throw new WordNotFoundException("word offer with " + id + " not found");
        }
        WordOffer wordOffer = wordOfferOptional.get();
        wordService.createNewWord(userDetails, new CreateWordRequest(wordOffer.getWord(), wordOffer.getDescription()));

        wordOffer.setApproved(true);
        wordOffer.setChecked(true);
        wordsOfferRepository.save(wordOffer);
    }

    @Transactional
    public void reject(UserDetails userDetails, Long id) {
        Admin admin = (Admin) userDetails;

        Optional<WordOffer> wordOfferOptional = wordsOfferRepository.findById(id);
        if (wordOfferOptional.isEmpty()) {
            throw new WordNotFoundException("word offer with " + id + " not found");
        }
        WordOffer wordOffer = wordOfferOptional.get();
        wordOffer.setApproved(false);
        wordOffer.setChecked(true);
        wordsOfferRepository.save(wordOffer);
        log.info("REJECT WORD Пользователь {} не добавил новое слово {}", admin.getEmail(), wordOffer.getWord());
    }
}
