package com.margot.word_map.config;

import com.margot.word_map.exception.ErrorCode;
import com.margot.word_map.utils.ErrorResponseFactory;
import com.margot.word_map.validation.ErrorCodeExtractor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class SpringMvcExceptionHandler extends ResponseEntityExceptionHandler {

    private final ErrorResponseFactory errorResponseFactory;
    private final ErrorCodeExtractor codeExtractor;

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        log.warn(ex.getMessage(), ex);

        FieldError error = ex.getBindingResult().getFieldErrors().getFirst();
        ErrorCode code = codeExtractor.extractCode(error);

        return ResponseEntity
                .status(status)
                .body(errorResponseFactory.build(
                        code,
                        request.getLocale()));
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        log.warn(ex.getMessage(), ex);

        return ResponseEntity
                .status(status)
                .body(errorResponseFactory.build(
                        ErrorCode.FORMAT_ERROR,
                        request.getLocale()));
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
