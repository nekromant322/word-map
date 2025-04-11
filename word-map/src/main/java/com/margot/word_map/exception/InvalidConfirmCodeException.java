package com.margot.word_map.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidConfirmCodeException extends BaseException {

    public InvalidConfirmCodeException() {
        super("invalid confirm code");
    }
}
