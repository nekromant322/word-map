package com.margot.word_map.exception;

public class PlatformNotFoundException extends BusinessException {

    private final static ErrorCode CODE = ErrorCode.PLATFORM_NOT_FOUND;

    public PlatformNotFoundException() {
        super(CODE);
    }

    public PlatformNotFoundException(String message) {
        super(message, CODE);
    }
}
