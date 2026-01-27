package com.margot.word_map.model.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.Optional;

public enum Role {

    @JsonProperty("admin")
    ADMIN,

    @JsonProperty("moderator")
    MODERATOR;

    public static Optional<Role> fromString(String value) {
        return Arrays.stream(values())
                .filter(f -> f.name().equalsIgnoreCase(value))
                .findFirst();
    }
}
