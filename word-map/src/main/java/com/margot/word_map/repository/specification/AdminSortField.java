package com.margot.word_map.repository.specification;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
public enum AdminSortField {
    ALPHABET("email"),
    ROLE("role"),
    DATE("dateActive");

    private final String entityFieldName;

    public static Optional<AdminSortField> fromString(String value) {
        return Arrays.stream(values())
                .filter(f -> f.name().equalsIgnoreCase(value))
                .findFirst();
    }
}
