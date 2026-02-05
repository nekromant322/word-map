package com.margot.word_map.exception;

public class WordNotFoundException extends BusinessException {

    private static final ErrorCode CODE = ErrorCode.WORD_NOT_FOUND;

    public WordNotFoundException() {
        super(CODE);
    }

    public WordNotFoundException(String message) {
        super(message, CODE);
    }
}
