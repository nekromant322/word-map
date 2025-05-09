package com.margot.word_map.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class InvalidTokenException extends BaseException {

    public InvalidTokenException() {
    }

    public InvalidTokenException(String message) {
        super(message);
    }
}