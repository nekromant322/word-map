package com.margot.word_map.model;

import com.fasterxml.jackson.annotation.JsonValue;
import com.margot.word_map.model.enums.PersistableEnum;

public enum WordOfferStatus implements PersistableEnum {
    CHECK("check"),
    APPROVED("approved"),
    REJECTED("rejected");

    private final String value;

    WordOfferStatus(String value) {
        this.value = value;
    }

    @JsonValue
    @Override
    public String getValue() {
        return value;
    }
}
