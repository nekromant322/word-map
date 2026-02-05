package com.margot.word_map.controller.advice;

import com.margot.word_map.dto.response.ErrorResponse;
import com.margot.word_map.exception.BusinessException;
import com.margot.word_map.exception.ErrorCode;
import com.margot.word_map.utils.ErrorResponseFactory;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Locale;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ErrorResponseFactory errorResponseFactory;

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex, Locale locale) {
        log.warn(ex.getMessage(), ex);

        return buildResponse(ex.getErrorCode(), locale);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, Locale locale) {
        log.warn("Fields validation error: ", ex);

        FieldError error = ex.getBindingResult()
                .getFieldErrors()
                .getFirst();
        ErrorCode code = ErrorCode.valueOf(error.getDefaultMessage());

        return buildResponse(code, locale);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(ConstraintViolationException ex, Locale locale) {
        log.warn("Fields validation error: ", ex);

        ConstraintViolation<?> violation = ex.getConstraintViolations().iterator().next();
        ErrorCode code = ErrorCode.valueOf(violation.getMessage());

        return buildResponse(code, locale);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUncaught(Exception ex, Locale locale) {
        log.error("Unhandled exception occurred: ", ex);

        return buildResponse(ErrorCode.INTERNAL_SERVER_ERROR, locale);
    }

    private ResponseEntity<ErrorResponse> buildResponse(ErrorCode errorCode, Locale locale) {
        return ResponseEntity.status(errorCode.getStatus())
                .body(errorResponseFactory.build(errorCode, locale));
    }
}
