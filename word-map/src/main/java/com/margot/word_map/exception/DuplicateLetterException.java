package com.margot.word_map.exception;

public class DuplicateLetterException extends BusinessException {

    private static final ErrorCode CODE = ErrorCode.DUPLICATE_LETTER;

    public DuplicateLetterException() {
        super(CODE);
    }

    public DuplicateLetterException(String message) {
        super(message, CODE);
    }
}
