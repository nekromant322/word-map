package com.margot.word_map.validation.annotation;

import com.margot.word_map.exception.ErrorCode;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.ReportAsSingleViolation;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@NotNull
@Pattern(regexp = "^\\d{6}$")
@Constraint(validatedBy = {})
@ReportAsSingleViolation
public @interface ConfirmCode {

    ErrorCode errorCode() default ErrorCode.CODE_INVALID;

    String message() default "Неверный формат кода подтверждения";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
