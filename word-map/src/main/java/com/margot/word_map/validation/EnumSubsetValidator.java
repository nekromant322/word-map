package com.margot.word_map.validation;

import com.margot.word_map.validation.annotation.EnumSubset;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.List;

public class EnumSubsetValidator implements ConstraintValidator<EnumSubset, Enum<?>> {

    private List<String> acceptedValues;

    @Override
    public void initialize(EnumSubset annotation) {
        acceptedValues = Arrays.stream(annotation.anyOf())
                .map(String::toUpperCase)
                .toList();
    }

    @Override
    public boolean isValid(Enum<?> value, ConstraintValidatorContext context) {
        if (value == null) return true;
        return acceptedValues.contains(value.name().toUpperCase());
    }
}
