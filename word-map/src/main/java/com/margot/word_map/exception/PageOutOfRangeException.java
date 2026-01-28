package com.margot.word_map.exception;

public class PageOutOfRangeException extends BusinessException {

    private static final ErrorCode CODE = ErrorCode.PAGE_OUT_OF_RANGE;

    public PageOutOfRangeException() {
        super(CODE);
    }

    public PageOutOfRangeException(String message) {
        super(message, CODE);
    }
}
