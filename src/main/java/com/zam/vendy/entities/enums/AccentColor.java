package com.zam.vendy.entities.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AccentColor {

    BRAND("brand"),
    WHATSAPP("whatsapp"),
    ROSE("rose"),
    AMBER("amber"),
    SKY("sky"),
    SLATE("slate"),
    // El hex real vive en Apariencia.accentColorHex; este valor solo marca que el
    // negocio eligió un color propio en vez de uno de la paleta predefinida.
    CUSTOM("custom");

    private final String value;

    AccentColor(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static AccentColor fromValue(String value) {
        for (AccentColor accentColor : values()) {
            if (accentColor.value.equalsIgnoreCase(value)) {
                return accentColor;
            }
        }
        throw new IllegalArgumentException("AccentColor inválido: " + value);
    }
}
