package com.margot.word_map.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class GridIsDuplicatedException extends BaseException {

    public GridIsDuplicatedException() {
    }

    public GridIsDuplicatedException(String message) {
        super(message);
    }
}
