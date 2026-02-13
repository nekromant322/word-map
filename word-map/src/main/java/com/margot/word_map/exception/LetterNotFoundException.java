package com.margot.word_map.exception;

public class LetterNotFoundException extends BusinessException {

    private static final ErrorCode CODE = ErrorCode.LETTER_NOT_FOUND;

    public LetterNotFoundException() {
        super(CODE);
    }

    public LetterNotFoundException(String message) {
        super(message, CODE);
    }
}
