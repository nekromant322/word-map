package com.margot.word_map.repository.specification;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ServerField {
    ID("id"),
    IS_OPEN("isOpen"),
    PLATFORM_ID("platform"),
    LANGUAGE_ID("language"),
    NAME("name");

    private final String entityFieldName;
}
