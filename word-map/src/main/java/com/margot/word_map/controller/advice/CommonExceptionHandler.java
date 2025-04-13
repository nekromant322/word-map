package com.margot.word_map.controller.advice;

import com.margot.word_map.dto.CommonErrorDto;
import com.margot.word_map.exception.BaseException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class CommonExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<CommonErrorDto> handleBaseException(BaseException e) {
        ResponseStatus responseStatus = e.getClass().getAnnotation(ResponseStatus.class);
        HttpStatus status = responseStatus != null ? responseStatus.value() : HttpStatus.INTERNAL_SERVER_ERROR;

        log.warn("Handled BaseException: ", e);
        return createErrorResponse(e, status);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonErrorDto> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.warn("error with validation request fields: ", ex);
        StringBuilder sb = new StringBuilder();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                sb.append(error.getField()).append(": ").append(error.getDefaultMessage()).append("\n")
        );

        return createErrorResponse(sb.toString(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<CommonErrorDto> handleValidationException(ConstraintViolationException ex) {
        log.warn("error with validation", ex);
        return createErrorResponse("error with validation", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<CommonErrorDto> handleUserNotFoundException(UsernameNotFoundException e) {
        log.warn("username not found exception");
        return createErrorResponse("user not found", HttpStatus.FORBIDDEN);
    }

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<CommonErrorDto> handleException(Exception e) {
//        log.error("Unhandled Exception: ", e);
//        return createErrorResponse("internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
//    }

    private ResponseEntity<CommonErrorDto> createErrorResponse(Exception e, HttpStatus status) {
        CommonErrorDto body = CommonErrorDto.builder()
                .code(status.value())
                .message(e.getMessage())
                .date(LocalDateTime.now())
                .build();

        return ResponseEntity.status(status).body(body);
    }

    private ResponseEntity<CommonErrorDto> createErrorResponse(String message, HttpStatus status) {
        CommonErrorDto body = CommonErrorDto.builder()
                .code(status.value())
                .message(message)
                .date(LocalDateTime.now())
                .build();

        return ResponseEntity.status(status).body(body);
    }
}
