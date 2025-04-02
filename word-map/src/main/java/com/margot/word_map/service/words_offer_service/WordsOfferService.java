package com.margot.word_map.service.words_offer_service;

import com.margot.word_map.model.WordOffer;
import com.margot.word_map.repository.WordsOfferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WordsOfferService {

    private final WordsOfferRepository wordsOfferRepository;
    private final WordRepository wordRepository;

    @Transactional
    public void save(WordOffer wordOffer) {
        wordsOfferRepository.save(wordOffer);
    }

    public WordOffer findByWord(String word) {
        return wordsOfferRepository.findByWord(word);
    }

    public Page<WordOffer> findAllByCheckedIsFalse(Pageable pageable) {
        return wordsOfferRepository.findAll(pageable);
    }

    @Transactional
    public void approve(Long id) {

        wordOffer.setApproved(true);
        wordOffer.setChecked(true);
        wordsOfferRepository.save(wordOffer);
        Word word = new Word();
        wordRepository.save(word);
    }

    @Transactional
    public void reject(Long id) {
        wordOffer.setApproved(false);
        wordOffer.setChecked(true);
        wordsOfferRepository.save(wordOffer);
    }
}
