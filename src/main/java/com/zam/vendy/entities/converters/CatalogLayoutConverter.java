package com.zam.vendy.entities.converters;

import com.zam.vendy.entities.enums.CatalogLayout;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class CatalogLayoutConverter implements AttributeConverter<CatalogLayout, String> {

    @Override
    public String convertToDatabaseColumn(CatalogLayout attribute) {
        return attribute != null ? attribute.getValue() : null;
    }

    @Override
    public CatalogLayout convertToEntityAttribute(String dbData) {
        return dbData != null ? CatalogLayout.fromValue(dbData) : null;
    }
}
