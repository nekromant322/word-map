package com.margot.word_map.model.converter;

import com.margot.word_map.model.enums.PersistableEnum;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@Converter(autoApply = true)
@RequiredArgsConstructor
public abstract class AbstractEnumConverter<T extends Enum<T> & PersistableEnum>
        implements AttributeConverter<T, String> {

    private final Class<T> clazz;

    @Override
    public String convertToDatabaseColumn(T attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public T convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        return Stream.of(clazz.getEnumConstants())
                .filter(e -> e.name().equals(dbData.toUpperCase()))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
