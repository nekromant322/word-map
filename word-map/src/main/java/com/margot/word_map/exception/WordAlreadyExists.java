package com.margot.word_map.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class WordAlreadyExists extends BaseException {

    public WordAlreadyExists() {
    }

    public WordAlreadyExists(String message) {
        super(message);
    }
}
