package com.margot.word_map.exception;

public class LanguageInActiveWorldException extends BusinessException {

    private final static ErrorCode CODE = ErrorCode.LANGUAGE_IN_ACTIVE_WORLD;

    public LanguageInActiveWorldException() {
        super(CODE);
    }

    public LanguageInActiveWorldException(String message) {
        super(message, CODE);
    }
}
