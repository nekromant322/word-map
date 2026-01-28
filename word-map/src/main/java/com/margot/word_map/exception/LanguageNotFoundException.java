package com.margot.word_map.exception;

public class LanguageNotFoundException extends BusinessException {

    private static final ErrorCode CODE = ErrorCode.NO_LANGUAGE_PERMISSION;

    public LanguageNotFoundException() {
        super(CODE);
    }

    public LanguageNotFoundException(String message) {
        super(message, CODE);
    }
}
