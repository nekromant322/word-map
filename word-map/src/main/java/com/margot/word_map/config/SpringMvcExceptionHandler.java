package com.margot.word_map.config;

import com.margot.word_map.exception.ErrorCode;
import com.margot.word_map.utils.ErrorResponseFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Locale;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class SpringMvcExceptionHandler extends ResponseEntityExceptionHandler {

    private final ErrorResponseFactory errorResponseFactory;

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        log.warn(ex.getMessage(), ex);

        FieldError error = ex.getBindingResult()
                .getFieldErrors()
                .getFirst();
        ErrorCode code = ErrorCode.valueOf(error.getDefaultMessage());

        Locale locale = request.getLocale();
        return ResponseEntity
                .status(code.getStatus())
                .body(errorResponseFactory.build(
                        code,
                        locale));
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex,
            Object body,
            HttpHeaders headers,
            HttpStatusCode statusCode,
            WebRequest request) {

        log.warn(ex.getMessage(), ex);

        return ResponseEntity
                .status(statusCode)
                .body(errorResponseFactory.build(
                        HttpStatus.valueOf(statusCode.value()),
                        ""));
    }
}
