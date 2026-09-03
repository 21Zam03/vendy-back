package com.zam.vendy.entities.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Font {

    SANS("sans"),
    SERIF("serif"),
    ROUNDED("rounded");

    private final String value;

    Font(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static Font fromValue(String value) {
        for (Font font : values()) {
            if (font.value.equalsIgnoreCase(value)) {
                return font;
            }
        }
        throw new IllegalArgumentException("Font inválido: " + value);
    }
}
