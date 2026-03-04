package com.margot.word_map.exception;

public class WordOfferAlreadyExistsException extends BusinessException {

    private static final ErrorCode CODE = ErrorCode.DUPLICATE_OFFER;

    public WordOfferAlreadyExistsException() {
        super(CODE);
    }

    public WordOfferAlreadyExistsException(String message) {
        super(message, CODE);
    }
}