package com.margot.word_map.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class WordNotFoundException extends BusinessException {

    private static final ErrorCode CODE = ErrorCode.WORD_NOT_FOUND;

    public WordNotFoundException() {
        super(CODE);
    }

    public WordNotFoundException(String message) {
        super(message, CODE);
    }
}
