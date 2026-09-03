package com.zam.vendy.entities.converters;

import com.zam.vendy.entities.enums.AccentColor;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class AccentColorConverter implements AttributeConverter<AccentColor, String> {

    @Override
    public String convertToDatabaseColumn(AccentColor attribute) {
        return attribute != null ? attribute.getValue() : null;
    }

    @Override
    public AccentColor convertToEntityAttribute(String dbData) {
        return dbData != null ? AccentColor.fromValue(dbData) : null;
    }
}
