package com.margot.word_map.exception;

public class PlatformAssignedToPlayersException extends BusinessException {

    private final static ErrorCode CODE = ErrorCode.PLATFORM_ASSIGNED_TO_PLAYERS;

    public PlatformAssignedToPlayersException() {
        super(CODE);
    }

    public PlatformAssignedToPlayersException(String message) {
        super(message, CODE);
    }
}
