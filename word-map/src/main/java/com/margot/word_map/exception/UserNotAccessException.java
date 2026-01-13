package com.margot.word_map.exception;

public class UserNotAccessException extends BusinessException {

    private final static ErrorCode CODE = ErrorCode.USER_NOT_ACCESS;

    public UserNotAccessException() {
        super(CODE);
    }

    public UserNotAccessException(String message) {
        super(message, CODE);
    }
}
