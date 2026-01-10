package com.margot.word_map.model;

import com.fasterxml.jackson.annotation.JsonValue;
import com.margot.word_map.model.enums.PersistableEnum;

public enum LetterType implements PersistableEnum {
    ALL("All"),
    VOWEL("Vowel"),
    CONSONANT("Consonant");

    private final String value;

    LetterType(String value) {
        this.value = value;
    }

    @JsonValue
    @Override
    public String getValue() {
        return value;
    }
}
