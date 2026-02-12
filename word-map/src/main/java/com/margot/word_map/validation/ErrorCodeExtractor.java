package com.margot.word_map.validation;

import com.margot.word_map.exception.ErrorCode;
import jakarta.validation.ConstraintViolation;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;

import java.util.Map;

@Component
public class ErrorCodeExtractor {

    public ErrorCode extractCode(FieldError fieldError) {
        ErrorCode code = ErrorCode.FORMAT_ERROR;

        try {
            ConstraintViolation<?> violation = fieldError.unwrap(ConstraintViolation.class);
            Map<String, Object> attributes = violation.getConstraintDescriptor().getAttributes();

            code = (ErrorCode) attributes.getOrDefault("errorCode", code);
        } catch (IllegalArgumentException ignored) {

        }

        return code;
    }
}
