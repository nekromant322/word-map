package com.margot.word_map.controller.advice;

import com.margot.word_map.dto.response.ErrorPayload;
import com.margot.word_map.dto.response.ErrorResponse;
import com.margot.word_map.exception.BusinessException;
import com.margot.word_map.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Locale;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex, Locale locale) {
        log.warn(ex.getMessage());

        ErrorCode errorCode = ex.getErrorCode();

        String message = messageSource.getMessage(
                errorCode.getPropertyName(),
                null,
                locale
        );

        ErrorPayload payload = new ErrorPayload(
                errorCode.name(),
                message,
                LocalDateTime.now());

        return ResponseEntity.status(errorCode.getStatus())
                .body(new ErrorResponse(payload));
    }
}
