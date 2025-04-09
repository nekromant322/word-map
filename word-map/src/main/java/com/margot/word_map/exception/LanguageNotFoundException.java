package com.margot.word_map.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class LanguageNotFoundException extends BaseException {

    public LanguageNotFoundException() {
    }

    public LanguageNotFoundException(String message) {
        super(message);
    }
}
