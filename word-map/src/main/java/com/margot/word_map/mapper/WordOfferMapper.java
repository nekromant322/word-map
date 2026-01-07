package com.margot.word_map.mapper;

import com.margot.word_map.dto.response.WordOfferResponse;
import com.margot.word_map.model.WordOffer;
import org.springframework.stereotype.Component;

@Component
public class WordOfferMapper {

    public WordOfferResponse toResponse(WordOffer wordOffer) {
        return WordOfferResponse.builder()
                .id(wordOffer.getId())
                .word(wordOffer.getWord())
                .userId(wordOffer.getUserId())
                .createdAt(wordOffer.getCreatedAt())
                .status(wordOffer.getStatus())
                .build();
    }
}
