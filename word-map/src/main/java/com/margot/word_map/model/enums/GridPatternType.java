package com.margot.word_map.model.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum GridPatternType {
    @JsonProperty("angular")
    ANGULAR,

    @JsonProperty("regular")
    REGULAR,
}
