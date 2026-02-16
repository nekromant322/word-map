package com.margot.word_map.exception;

public class PatternNotFoundException extends BusinessException {

    private static final ErrorCode CODE = ErrorCode.PATTERN_NOT_FOUND;

    public PatternNotFoundException() {
        super(CODE);
    }

    public PatternNotFoundException(String message) {
        super(message, CODE);
    }
}
