package com.margot.word_map.exception;

public class ConfirmNotFoundException extends BusinessException {

    private static final ErrorCode CODE = ErrorCode.CONFIRM_NOT_FOUND;

    public ConfirmNotFoundException() {
        super(CODE);
    }

    public ConfirmNotFoundException(String message) {
        super(message, CODE);
    }
}
