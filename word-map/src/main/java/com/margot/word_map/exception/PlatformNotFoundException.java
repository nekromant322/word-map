package com.margot.word_map.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PlatformNotFoundException extends BaseException {

    public PlatformNotFoundException() {
    }

    public PlatformNotFoundException(String message) {
        super(message);
    }
}
