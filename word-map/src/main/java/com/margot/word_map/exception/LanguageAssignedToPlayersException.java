package com.margot.word_map.exception;

public class LanguageAssignedToPlayersException extends BusinessException {

    private final static ErrorCode CODE = ErrorCode.LANGUAGE_ASSIGNED_TO_PLAYERS;

    public LanguageAssignedToPlayersException(String message) {
        super(message, CODE);
    }

    public LanguageAssignedToPlayersException() {
        super(CODE);
    }
}
