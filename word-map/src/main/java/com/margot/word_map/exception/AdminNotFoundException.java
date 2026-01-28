package com.margot.word_map.exception;

public class AdminNotFoundException extends BusinessException {

    private static final ErrorCode CODE = ErrorCode.ADMIN_NOT_FOUND;

    public AdminNotFoundException() {
        super(CODE);
    }

    public AdminNotFoundException(String message) {
        super(message, CODE);
    }
}
