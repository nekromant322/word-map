package com.margot.word_map.exception;

public class AuditSaveException extends RuntimeException {

    public AuditSaveException() {
    }

    public AuditSaveException(String message, Throwable cause) {
        super(message, cause);
    }
}
