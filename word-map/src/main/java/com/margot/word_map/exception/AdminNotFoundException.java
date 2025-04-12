package com.margot.word_map.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class AdminNotFoundException extends BaseException {

    public AdminNotFoundException() {
    }

    public AdminNotFoundException(String message) {
        super(message);
    }
}
