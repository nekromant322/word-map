package com.margot.word_map.exception;

public class PlatformInActiveWorldException extends BusinessException {

    private final static ErrorCode CODE = ErrorCode.PLATFORM_IN_ACTIVE_WORLD;

    public PlatformInActiveWorldException() {
        super(CODE);
    }

    public PlatformInActiveWorldException(String message) {
        super(message, CODE);
    }
}
