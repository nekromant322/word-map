package com.margot.word_map.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum BonusType {
    @JsonProperty("Letter")
    LETTER,

    @JsonProperty("Word")
    WORD
}
