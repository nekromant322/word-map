package com.margot.word_map.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class WordAlreadyExists extends BusinessException {

    private static final ErrorCode CODE = ErrorCode.DUPLICATE_WORD;

    public WordAlreadyExists() {
        super(CODE);
    }

    public WordAlreadyExists(String message) {
        super(message, CODE);
    }
}
