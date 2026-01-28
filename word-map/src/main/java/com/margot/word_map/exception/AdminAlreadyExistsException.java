package com.margot.word_map.exception;

public class AdminAlreadyExistsException extends BusinessException {

    private static final ErrorCode CODE = ErrorCode.DUPLICATE_EMAIL;

    public AdminAlreadyExistsException() {
        super(CODE);
    }

    public AdminAlreadyExistsException(String message) {
        super(message, CODE);
    }
}
