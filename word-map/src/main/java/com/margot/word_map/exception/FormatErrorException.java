package com.margot.word_map.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class FormatErrorException extends BusinessException {

    private static final ErrorCode CODE = ErrorCode.FORMAT_ERROR;

    public FormatErrorException() {
        super(CODE);
    }

    public FormatErrorException(String message) {
        super(message, CODE);
    }
}
