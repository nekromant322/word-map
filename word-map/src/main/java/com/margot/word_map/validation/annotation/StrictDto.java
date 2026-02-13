package com.margot.word_map.validation.annotation;

import com.margot.word_map.exception.ErrorCode;
import com.margot.word_map.validation.RequiredFieldsValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.ReportAsSingleViolation;
import jakarta.validation.constraints.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@NotNull
@Constraint(validatedBy = { RequiredFieldsValidator.class })
@ReportAsSingleViolation
public @interface StrictDto {

    ErrorCode errorCode() default ErrorCode.MISSING_REQUIRED_FIELDS;

    String message() default "Обязательное поле {fieldName} отсутствует";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
