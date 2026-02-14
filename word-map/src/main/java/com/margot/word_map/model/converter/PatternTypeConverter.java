package com.margot.word_map.model.converter;

import com.margot.word_map.model.enums.PatternType;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PatternTypeConverter extends AbstractEnumConverter<PatternType> {

    public PatternTypeConverter() {
        super(PatternType.class);
    }
}
