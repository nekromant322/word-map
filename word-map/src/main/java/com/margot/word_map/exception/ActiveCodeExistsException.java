package com.margot.word_map.exception;

public class ActiveCodeExistsException extends BusinessException {

    private static final ErrorCode CODE = ErrorCode.ACTIVE_CODE_EXISTS;

    public ActiveCodeExistsException() {
        super(CODE);
    }
}
