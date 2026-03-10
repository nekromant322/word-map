package com.margot.word_map.exception;

public class DuplicateServerException extends BusinessException {

    private static final ErrorCode CODE = ErrorCode.DUPLICATE_SERVER;

    public DuplicateServerException() {
        super(CODE);
    }

    public DuplicateServerException(String message) {
        super(message, CODE);
    }
}
