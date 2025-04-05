package com.margot.word_map.exception.handler;

import com.margot.word_map.exception.*;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleValidationException(ConstraintViolationException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(LanguageNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleLanguageNotFoundException(LanguageNotFoundException e) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "language not found");
        errorResponse.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(WordNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleWordNotFoundException(WordNotFoundException e) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "word not found");
        errorResponse.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(WordAlreadyExists.class)
    public ResponseEntity<Map<String, String>> handleWordAlreadyExists(WordAlreadyExists e) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "word already exists");
        errorResponse.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(AdminNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleAdminNotFoundException(AdminNotFoundException e) {
        Map<String, String> errorResponses = new HashMap<>();
        errorResponses.put("error", "admin not found");
        errorResponses.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponses);
    }

    @ExceptionHandler(AdminAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleAdminAlreadyExistsException(AdminAlreadyExistsException e) {
        Map<String, String> errorResponses = new HashMap<>();
        errorResponses.put("error", "admin already exists");
        errorResponses.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponses);
    }

    @ExceptionHandler(RoleNotBelongToAdminException.class)
    public ResponseEntity<Map<String, String>> handleRoleNotBelongToAdminException(RoleNotBelongToAdminException e) {
        Map<String, String> errorResponses = new HashMap<>();
        errorResponses.put("error", "role not belong to admin");
        errorResponses.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponses);
    }

    @ExceptionHandler(NotRightRoleLevelException.class)
    public ResponseEntity<Map<String, String>> handleNotRightRoleLevelException(NotRightRoleLevelException e) {
        Map<String, String> errorResponses = new HashMap<>();
        errorResponses.put("error", "not right role level");
        errorResponses.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponses);
    }

    @ExceptionHandler(MismatchAdminTypeException.class)
    public ResponseEntity<Map<String, String>> handleMismatchAdminTypeException(MismatchAdminTypeException e) {
        Map<String, String> errorResponses = new HashMap<>();
        errorResponses.put("error", "not right role level");
        errorResponses.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponses);
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<Map<String, String>> handleTokenExpiredException(TokenExpiredException e) {
        log.warn("Expired JWT token: {}", e.getMessage());
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Token expired");
        errorResponse.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<Map<String, String>> handleInvalidTokenException(InvalidTokenException e) {
        log.warn("Invalid JWT token: {}", e.getMessage());
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Invalid token");
        errorResponse.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUserNotFoundException(UsernameNotFoundException e) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "user_not_found");
        errorResponse.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }
}
