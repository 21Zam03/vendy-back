package com.zam.vendy.entities.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Radius {

    SQUARE("square"),
    SOFT("soft"),
    ROUND("round");

    private final String value;

    Radius(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static Radius fromValue(String value) {
        for (Radius radius : values()) {
            if (radius.value.equalsIgnoreCase(value)) {
                return radius;
            }
        }
        throw new IllegalArgumentException("Radius inválido: " + value);
    }
}
