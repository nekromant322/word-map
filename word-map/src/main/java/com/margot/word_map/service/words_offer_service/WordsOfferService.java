package com.margot.word_map.service.words_offer_service;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.margot.word_map.dto.request.CreateWordRequest;
import com.margot.word_map.dto.response.WordOfferResponse;
import com.margot.word_map.model.Admin;
import com.margot.word_map.model.WordOffer;
import com.margot.word_map.repository.WordRepository;
import com.margot.word_map.repository.WordsOfferRepository;
import com.margot.word_map.service.WordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class WordsOfferService {

    private final WordsOfferRepository wordsOfferRepository;
    private final WordService wordService;
    private final WordRepository wordRepository;

    @Transactional
    public void save(WordOffer wordOffer) {
        wordsOfferRepository.save(wordOffer);
    }

    public boolean findByWordInTableWords(String word) {
        return wordRepository.findWordByWord(word).isPresent();
    }

    public boolean findByWordInTableWordsOffer(String word) {
        return wordsOfferRepository.findByWord(word).isPresent();
    }

    public StreamingResponseBody getAllWordsOffersNotChecked() {
        return outputStream -> {
            try (JsonGenerator generator = new ObjectMapper().getFactory().createGenerator(outputStream)) {
                generator.writeStartArray();

                int page = 0;
                int pageSize = 20;

                Page<WordOffer> wordsOfferPage;
                do {
                    wordsOfferPage = wordsOfferRepository.findAllByCheckedIsFalse(PageRequest.of(page, pageSize));

                    for (WordOffer wordOffer : wordsOfferPage) {
                        generator.writeObject(new WordOfferResponse(wordOffer));
                    }
                    page++;
                } while (!wordsOfferPage.isLast());
                generator.writeEndArray();
            }
        };
    }

    //Потом добавим еще и юзера, чтобы считать сколько слов добавил
    @Transactional
    public void approve(UserDetails userDetails, Long id) {
        WordOffer wordOffer = wordsOfferRepository.findById(id).get();
        wordService.createNewWord(userDetails, new CreateWordRequest(wordOffer.getWord(), wordOffer.getDescription()));

        wordOffer.setApproved(true);
        wordOffer.setChecked(true);
        wordsOfferRepository.save(wordOffer);
    }

    @Transactional
    public void reject(UserDetails userDetails, Long id) {
        Admin admin = (Admin) userDetails;

        WordOffer wordOffer = wordsOfferRepository.findById(id).get();
        wordOffer.setApproved(false);
        wordOffer.setChecked(true);
        wordsOfferRepository.save(wordOffer);
        log.info("REJECT WORD Пользователь {} не добавил новое слово {}", admin.getEmail(), wordOffer.getWord());
    }
}
