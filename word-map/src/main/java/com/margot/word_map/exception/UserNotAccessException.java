package com.margot.word_map.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UserNotAccessException extends BaseException {

    public UserNotAccessException() {
    }

    public UserNotAccessException(String message) {
        super(message);
    }
}
