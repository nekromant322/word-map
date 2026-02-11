package com.margot.word_map.validation.annotation;

import com.margot.word_map.exception.ErrorCode;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.ReportAsSingleViolation;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Email
@NotBlank
@Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
@ReportAsSingleViolation
public @interface ValidEmail {

    ErrorCode errorCode() default ErrorCode.EMAIL_FORMAT_ERROR;

    String message() default "Неверный формат почты";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
