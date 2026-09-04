package com.zam.vendy.entities.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Cover {

    SOLID("solid"),
    GRADIENT("gradient"),
    IMAGEN("imagen"),
    NAVIDAD("navidad"),
    HALLOWEEN("halloween"),
    SAN_VALENTIN("san_valentin"),
    VERANO("verano"),
    BLACK_FRIDAY("black_friday"),
    ANO_NUEVO("ano_nuevo");

    private final String value;

    Cover(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static Cover fromValue(String value) {
        for (Cover cover : values()) {
            if (cover.value.equalsIgnoreCase(value)) {
                return cover;
            }
        }
        throw new IllegalArgumentException("Cover inválido: " + value);
    }
}
