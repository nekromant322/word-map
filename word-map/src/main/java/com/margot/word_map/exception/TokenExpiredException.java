package com.margot.word_map.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class TokenExpiredException extends BaseException {

    public TokenExpiredException() {
    }

    public TokenExpiredException(String message) {
        super(message);
    }
}