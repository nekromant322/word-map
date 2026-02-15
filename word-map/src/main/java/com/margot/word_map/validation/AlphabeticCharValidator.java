package com.margot.word_map.validation;

import com.margot.word_map.validation.annotation.OnlyLetter;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AlphabeticCharValidator implements ConstraintValidator<OnlyLetter, Character> {

    @Override
    public boolean isValid(Character value, ConstraintValidatorContext context) {
        if (value == null) return true;
        return Character.isLetter(value);
    }
}
