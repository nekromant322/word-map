package com.margot.word_map.exception;

public class UserNotFoundException extends BusinessException {

    private final static ErrorCode CODE = ErrorCode.USER_NOT_FOUND;

    public UserNotFoundException() {
        super(CODE);
    }

    public UserNotFoundException(String message) {
        super(message, CODE);
    }
}
