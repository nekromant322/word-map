package com.margot.word_map.exception;

public class InvalidConfirmCodeException extends BusinessException {

    public InvalidConfirmCodeException() {
        super(ErrorCode.CODE_INVALID);
    }

    public InvalidConfirmCodeException(ErrorCode code) {
        super(code);
    }
}
