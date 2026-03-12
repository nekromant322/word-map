package com.margot.word_map.exception;

public class ServerCleanupInProgressException extends BusinessException {

    private static final ErrorCode CODE = ErrorCode.SERVER_CLEANUP_IN_PROGRESS;

    public ServerCleanupInProgressException() {
        super(CODE);
    }

    public ServerCleanupInProgressException(String message) {
        super(message, CODE);
    }
}