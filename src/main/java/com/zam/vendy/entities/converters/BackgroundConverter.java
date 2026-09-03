package com.zam.vendy.entities.converters;

import com.zam.vendy.entities.enums.Background;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class BackgroundConverter implements AttributeConverter<Background, String> {

    @Override
    public String convertToDatabaseColumn(Background attribute) {
        return attribute != null ? attribute.getValue() : null;
    }

    @Override
    public Background convertToEntityAttribute(String dbData) {
        return dbData != null ? Background.fromValue(dbData) : null;
    }
}
