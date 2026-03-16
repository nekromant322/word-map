package com.margot.word_map.dto.request;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Root;
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

    public Order toOrder(CriteriaBuilder cb, Root<?> root) {
        return direction == Sort.Direction.ASC
                ? cb.asc(root.get(field))
                : cb.desc(root.get(field));
    }
}
