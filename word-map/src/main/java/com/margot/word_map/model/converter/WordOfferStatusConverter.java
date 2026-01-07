package com.margot.word_map.model.converter;

import com.margot.word_map.model.WordOfferStatus;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class WordOfferStatusConverter extends AbstractEnumConverter<WordOfferStatus> {

    public WordOfferStatusConverter() {
        super(WordOfferStatus.class);
    }
}
