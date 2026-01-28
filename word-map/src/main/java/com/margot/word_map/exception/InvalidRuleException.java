package com.margot.word_map.exception;

public class InvalidRuleException extends BusinessException {

    private static final ErrorCode CODE = ErrorCode.INVALID_RULE;

    public InvalidRuleException() {
        super(CODE);
    }

    public InvalidRuleException(String message) {
        super(message, CODE);
    }
}
