package com.margot.word_map.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Sort;

@AllArgsConstructor
@Getter
public enum ServerSortingType {
    DATE_LATE("createdAt", Sort.Direction.DESC),
    DATE_EARLY("createdAt", Sort.Direction.ASC);

    private final String field;
    private final Sort.Direction direction;

    public Sort toSort() {
        return Sort.by(direction, field);
    }
}
