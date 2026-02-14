package com.margot.word_map.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PatternType implements PersistableEnum {
    START("start"),
    ANGULAR("angular"),
    REGULAR("regular");

    private final String value;

    PatternType(String value) {
        this.value = value;
    }

    @JsonValue
    @Override
    public String getValue() {
        return value;
    }
}
