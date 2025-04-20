package com.margot.word_map.service.auth.generic_auth;

public interface AuthEntityService<T> {
    T getByEmail(String email);
    Long getId(T entity);
    boolean hasAccess(T entity);
    String getEmail(T entity);
}

