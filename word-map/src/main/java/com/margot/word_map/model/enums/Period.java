package com.margot.word_map.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Period implements PersistableEnum {
    DAY("day"),
    WEEK("week"),
    MONTH("month"),
    ALL("all");

    private final String value;

    Period(String value) {
        this.value = value;
    }

    @JsonValue
    @Override
    public String getValue() {
        return value;
    }
}
