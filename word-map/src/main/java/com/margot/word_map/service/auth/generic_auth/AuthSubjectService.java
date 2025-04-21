package com.margot.word_map.service.auth.generic_auth;

import com.margot.word_map.exception.BaseException;

import java.util.List;

public interface AuthSubjectService<T> {

    T getByEmail(String email);

    Long getId(T entity);

    boolean hasAccess(T entity);

    String getEmail(T entity);

    T getEntityById(Long id);

    BaseException createNoAccessException(String email);

    String extractRole(T entity);

    List<String> extractRules(T entity);
}

