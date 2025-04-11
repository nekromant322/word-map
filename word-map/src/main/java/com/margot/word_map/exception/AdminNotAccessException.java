package com.margot.word_map.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class AdminNotAccessException extends BaseException {

    public AdminNotAccessException() {
    }

    public AdminNotAccessException(String message) {
        super(message);
    }
}
