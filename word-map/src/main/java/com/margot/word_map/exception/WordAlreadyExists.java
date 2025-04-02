package com.margot.word_map.exception;

public class WordAlreadyExists extends RuntimeException {
    public WordAlreadyExists(String message) {
        super(message);
    }
}
