package com.margot.word_map.mapper;

import com.margot.word_map.dto.response.WordOfferResponse;
import com.margot.word_map.model.WordOffer;

public class WordOfferMapper {

    public static WordOfferResponse toResponse(WordOffer wordOffer) {
        return new WordOfferResponse(wordOffer.getId(), wordOffer.getWord());
    }
}
