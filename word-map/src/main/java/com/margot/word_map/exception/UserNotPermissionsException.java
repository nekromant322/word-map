package com.margot.word_map.exception;

public class UserNotPermissionsException extends BusinessException {

    private static final ErrorCode CODE = ErrorCode.USER_NOT_PERMISSIONS;

    public UserNotPermissionsException() {
        super(CODE);
    }

    public UserNotPermissionsException(String message) {
        super(message, CODE);
    }
}
