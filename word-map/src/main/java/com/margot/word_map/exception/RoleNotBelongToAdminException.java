package com.margot.word_map.exception;

public class RoleNotBelongToAdminException extends RuntimeException {
    public RoleNotBelongToAdminException(String message) {
        super(message);
    }
}
