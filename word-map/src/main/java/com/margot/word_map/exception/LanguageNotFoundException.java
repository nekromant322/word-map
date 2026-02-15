package com.margot.word_map.exception;

public class LanguageNotFoundException extends BusinessException {

    private static final ErrorCode CODE = ErrorCode.LANGUAGE_NOT_FOUND;

    public LanguageNotFoundException() {
        super(CODE);
    }

    public LanguageNotFoundException(String message) {
        super(message, CODE);
    }
}
