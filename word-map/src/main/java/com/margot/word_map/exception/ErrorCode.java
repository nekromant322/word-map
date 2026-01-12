package com.margot.word_map.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    EMAIL_FORMAT_ERROR(HttpStatus.BAD_REQUEST, "error.email.format_error"),
    FORMAT_ERROR(HttpStatus.BAD_REQUEST, "error.format.error"),
    CODE_INVALID(HttpStatus.BAD_REQUEST, "error.code.invalid"),
    MISSING_REQUIRED_FIELDS(HttpStatus.BAD_REQUEST, "error.missing.required_fields"),
    PAGE_OUT_OF_RANGE(HttpStatus.BAD_REQUEST, "error.page.out_of_range"),

    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "error.invalid.refresh_token"),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "error.invalid.access_token"),

    USER_NOT_ACCESS(HttpStatus.FORBIDDEN, "error.user.not_access"),
    NO_LANGUAGE_PERMISSION(HttpStatus.FORBIDDEN, "error.language.no_permission"),
    USER_NOT_PERMISSIONS(HttpStatus.FORBIDDEN, "error.user.not_permissions"),

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "error.user.not_found"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "error.resource.not_found"),
    CONFIRM_NOT_FOUND(HttpStatus.NOT_FOUND, "error.confirm.not_found"),
    LANGUAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "error.language.not_found"),
    PLATFORM_NOT_FOUND(HttpStatus.NOT_FOUND, "error.platform.not_found"),
    LETTER_NOT_FOUND(HttpStatus.NOT_FOUND, "error.letter.not_found"),
    INVALID_RULE(HttpStatus.NOT_FOUND, "error.rule.invalid"),

    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "error.duplicate.email"),
    DUPLICATE_WORD(HttpStatus.CONFLICT, "error.duplicate.word"),
    DUPLICATE_PREFIX(HttpStatus.CONFLICT, "error.duplicate.prefix"),
    DUPLICATE_NAME(HttpStatus.CONFLICT, "error.duplicate.name"),
    DUPLICATE_OFFER(HttpStatus.CONFLICT, "error.duplicate.offer"),
    DUPLICATE_LETTER(HttpStatus.CONFLICT, "error.duplicate.letter"),

    INVALID_CONDITION(HttpStatus.CONFLICT, "error.condition.invalid"),
    LINE_IN_USE(HttpStatus.CONFLICT, "error.line.in_use"),
    LANGUAGE_IN_ACTIVE_WORLD(HttpStatus.CONFLICT, "error.language.in_active_world"),
    LANGUAGE_ASSIGNED_TO_PLAYERS(HttpStatus.CONFLICT, "error.language.assigned_to_players"),
    PLATFORM_IN_ACTIVE_WORLD(HttpStatus.CONFLICT, "error.platform.in_active_world"),
    PLATFORM_ASSIGNED_TO_PLAYERS(HttpStatus.CONFLICT, "error.platform.assigned_to_players"),

    CODE_SPOILED(HttpStatus.GONE, "error.code.spoiled"),
    ACTIVE_CODE_EXISTS(HttpStatus.TOO_MANY_REQUESTS, "error.code.active_exists"),

    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "error.unauthorized"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "error.server.internal");

    private final HttpStatus status;
    private final String propertyName;

    ErrorCode(HttpStatus status, String propertyName) {
        this.status = status;
        this.propertyName = propertyName;
    }
}