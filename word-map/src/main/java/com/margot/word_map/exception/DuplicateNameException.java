package com.margot.word_map.exception;

public class DuplicateNameException extends BusinessException {

    private static final ErrorCode CODE = ErrorCode.DUPLICATE_NAME;

    public DuplicateNameException() {
        super(CODE);
    }

    public DuplicateNameException(String message) {
        super(message, CODE);
    }
}
