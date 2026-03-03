package com.margot.word_map.dto.request;

import org.springframework.data.domain.Sort;

public enum WordSortingType {
    DATE_LATE("createdAt", Sort.Direction.DESC),
    DATE_EARLY("createdAt", Sort.Direction.ASC),
    RATING_HIGH("rating", Sort.Direction.DESC),
    RATING_LOW("rating", Sort.Direction.ASC);

    private final String field;
    private final Sort.Direction direction;

    WordSortingType(String field, Sort.Direction direction) {
        this.field = field;
        this.direction = direction;
    }

    public Sort toSort() {
        return Sort.by(direction, field);
    }
}
