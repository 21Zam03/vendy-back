package com.zam.vendy.entities.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

// Plantilla de página pública según el rubro del negocio. Es independiente de
// catalogLayout: la plantilla preconfigura un catalogLayout por defecto al elegirla,
// pero el usuario puede seguir cambiándolo manualmente después.
public enum Plantilla {

    MODA("moda"),
    COMIDA("comida"),
    BELLEZA("belleza"),
    ACCESORIOS("accesorios"),
    CALZADO("calzado"),
    BARBERIA("barberia");

    private final String value;

    Plantilla(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static Plantilla fromValue(String value) {
        for (Plantilla plantilla : values()) {
            if (plantilla.value.equalsIgnoreCase(value)) {
                return plantilla;
            }
        }
        throw new IllegalArgumentException("Plantilla inválida: " + value);
    }
}
