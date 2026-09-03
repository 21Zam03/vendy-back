package com.zam.vendy.entities.converters;

import com.zam.vendy.entities.enums.Radius;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class RadiusConverter implements AttributeConverter<Radius, String> {

    @Override
    public String convertToDatabaseColumn(Radius attribute) {
        return attribute != null ? attribute.getValue() : null;
    }

    @Override
    public Radius convertToEntityAttribute(String dbData) {
        return dbData != null ? Radius.fromValue(dbData) : null;
    }
}
