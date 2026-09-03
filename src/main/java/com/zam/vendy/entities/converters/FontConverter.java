package com.zam.vendy.entities.converters;

import com.zam.vendy.entities.enums.Font;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class FontConverter implements AttributeConverter<Font, String> {

    @Override
    public String convertToDatabaseColumn(Font attribute) {
        return attribute != null ? attribute.getValue() : null;
    }

    @Override
    public Font convertToEntityAttribute(String dbData) {
        return dbData != null ? Font.fromValue(dbData) : null;
    }
}
