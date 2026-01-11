package com.margot.word_map.exception;

public class InvalidTokenException extends BusinessException {

    public InvalidTokenException() {
        this("Invalid access token");
    }

    public InvalidTokenException(String message) {
        super(message, ErrorCode.INVALID_ACCESS_TOKEN);
    }
}