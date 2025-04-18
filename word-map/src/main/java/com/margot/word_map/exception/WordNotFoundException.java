package com.margot.word_map.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class WordNotFoundException extends BaseException {

    public WordNotFoundException() {
    }

    public WordNotFoundException(String message) {
        super(message);
    }
}
