package com.margot.word_map.validation.annotation;

import com.margot.word_map.validation.AlphabeticCharValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AlphabeticCharValidator.class)
public @interface OnlyLetter {

    String message() default "Символ должен быть буквенным";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}