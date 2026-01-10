package com.margot.word_map.model.converter;

import com.margot.word_map.model.LetterType;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class LetterTypeConverter extends AbstractEnumConverter<LetterType> {

    public LetterTypeConverter() {
        super(LetterType.class);
    }
}
