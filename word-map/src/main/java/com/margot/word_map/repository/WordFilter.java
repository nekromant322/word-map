package com.margot.word_map.repository;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@RequiredArgsConstructor
public class WordFilter {
    List<Predicate> predicates = new ArrayList<>();

    private final CriteriaBuilder cb;

    public <T> WordFilter add(T object, Function<T, Predicate> function) {
        if (object != null) {
            predicates.add(function.apply(object));
        }
        return this;
    }

    public static WordFilter builder(CriteriaBuilder cb) {
        return new WordFilter(cb);
    }

    public Predicate buildAnd() {
        return cb.and(predicates.toArray(new Predicate[0]));
    }

    public Predicate buildOr() {
        return cb.or(predicates.toArray(new Predicate[0]));
    }
}
