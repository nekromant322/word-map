package com.margot.word_map.exception;

public class ServerNotFoundException extends BusinessException {

    private static final ErrorCode CODE = ErrorCode.SERVER_NOT_FOUND;

    public ServerNotFoundException() {
        super(CODE);
    }

    public ServerNotFoundException(String message) {
        super(message, CODE);
    }
}