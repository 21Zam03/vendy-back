package com.zam.vendy.entities.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Background {

    WHITE("white"),
    SLATE("slate"),
    CREAM("cream"),
    MINT("mint"),
    LAVENDER("lavender");

    private final String value;

    Background(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static Background fromValue(String value) {
        for (Background background : values()) {
            if (background.value.equalsIgnoreCase(value)) {
                return background;
            }
        }
        throw new IllegalArgumentException("Background inválido: " + value);
    }
}
