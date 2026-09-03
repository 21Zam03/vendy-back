package com.zam.vendy.entities.converters;

import com.zam.vendy.entities.enums.Cover;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class CoverConverter implements AttributeConverter<Cover, String> {

    @Override
    public String convertToDatabaseColumn(Cover attribute) {
        return attribute != null ? attribute.getValue() : null;
    }

    @Override
    public Cover convertToEntityAttribute(String dbData) {
        return dbData != null ? Cover.fromValue(dbData) : null;
    }
}
