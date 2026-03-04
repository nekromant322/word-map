package com.margot.word_map.exception;

public class InvalidConditionException extends BusinessException {

    private static final ErrorCode CODE = ErrorCode.INVALID_CONDITION;

    public InvalidConditionException() {
        super(CODE);
    }

    public InvalidConditionException(String message) {
        super(message, CODE);
    }
}