package com.margot.word_map.exception;

public class DuplicatePrefixException extends BusinessException {

    private final static ErrorCode CODE = ErrorCode.DUPLICATE_PREFIX;

    public DuplicatePrefixException() {
        super(CODE);
    }

    public DuplicatePrefixException(String message) {
        super(message, CODE);
    }
}
