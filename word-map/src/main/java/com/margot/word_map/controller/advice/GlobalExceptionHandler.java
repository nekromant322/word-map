package com.margot.word_map.controller.advice;

import com.margot.word_map.dto.response.ErrorResponse;
import com.margot.word_map.exception.BusinessException;
import com.margot.word_map.exception.ErrorCode;
import com.margot.word_map.utils.ErrorResponseFactory;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.Parameter;
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

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            ConstraintViolationException ex,
            HandlerMethod handlerMethod,
            Locale locale) {
        log.warn("Ошибка валидации полей: ", ex);

        if (isControllerValidation(handlerMethod)) {
            return buildResponse(ErrorCode.FORMAT_ERROR, locale);
        }

        return buildResponse(ErrorCode.INTERNAL_SERVER_ERROR, locale);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, Locale locale) {
        log.warn(ex.getMessage(), ex);

        return buildResponse(ErrorCode.USER_NOT_PERMISSIONS, locale);
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

    private boolean isControllerValidation(HandlerMethod handlerMethod) {
        
        if (handlerMethod.getMethodAnnotation(Validated.class) != null) {
            return true;
        }

        Parameter[] parameters = handlerMethod.getMethod().getParameters();
        for (var param : parameters) {
            if (param.isAnnotationPresent(Valid.class)) {
                return true;
            }
        }

        return false;
    }
}
