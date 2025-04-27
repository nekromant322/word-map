package com.margot.word_map.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AdminAlreadyExistsException extends BaseException {

    public AdminAlreadyExistsException() {
    }

    public AdminAlreadyExistsException(String message) {
        super(message);
    }
}
