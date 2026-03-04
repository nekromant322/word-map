package com.margot.word_map.exception;

public class InvalidTokenException extends BusinessException {

    public InvalidTokenException() {
        this("Токен доступа недействителен");
    }

    public InvalidTokenException(String message) {
        super(message, ErrorCode.INVALID_ACCESS_TOKEN);
    }
}