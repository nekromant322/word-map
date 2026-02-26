package com.margot.word_map.exception;

public class RefreshTokenException extends BusinessException {

    public RefreshTokenException() {
        this("Сессия не найдена или истекла");
    }

    public RefreshTokenException(String message) {
        super(message, ErrorCode.INVALID_REFRESH_TOKEN);
    }
}
