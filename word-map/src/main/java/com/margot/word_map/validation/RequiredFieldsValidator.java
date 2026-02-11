package com.margot.word_map.validation;

import com.margot.word_map.validation.annotation.StrictDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Field;

public class RequiredFieldsValidator implements ConstraintValidator<StrictDto, Object> {

    @Override
    public boolean isValid(Object dto, ConstraintValidatorContext context) {
        if (dto == null) return true;

        context.disableDefaultConstraintViolation();

        boolean isValid = true;

        Field[] fields = dto.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Object value = field.get(dto);
                String fieldName = field.getName();

                boolean isInvalid = false;

                if (value == null) {
                    isInvalid = true;
                } else if (value instanceof String && ((String) value).isBlank()) {
                    isInvalid = true;
                }

                if (isInvalid) {
                    isValid = false;

                    context.buildConstraintViolationWithTemplate(
                            context.getDefaultConstraintMessageTemplate()
                                .replace("{fieldName}", fieldName))
                                .addPropertyNode(fieldName)
                                .addConstraintViolation();
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        return isValid;
    }
}
