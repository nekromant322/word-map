package com.margot.word_map.utils;

import com.margot.word_map.dto.response.ErrorPayload;
import com.margot.word_map.dto.response.ErrorResponse;
import com.margot.word_map.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class ErrorResponseFactory {

    private final MessageSource messageSource;

    public ErrorResponse build(ErrorCode errorCode, Locale locale) {
        String message = messageSource.getMessage(
                errorCode.getPropertyName(),
                null,
                locale);

        ErrorPayload payload = new ErrorPayload(
                errorCode.name(),
                message,
                LocalDateTime.now());

        return new ErrorResponse(payload);
    }

    public ErrorResponse build(HttpStatus status, String message) {
        ErrorPayload payload = new ErrorPayload(
                status.name(),
                message,
                LocalDateTime.now());

        return new ErrorResponse(payload);
    }
}
