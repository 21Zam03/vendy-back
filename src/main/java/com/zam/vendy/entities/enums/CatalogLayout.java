package com.zam.vendy.entities.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CatalogLayout {

    GRID("grid"),
    PRO("pro"),
    LIST("list");

    private final String value;

    CatalogLayout(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static CatalogLayout fromValue(String value) {
        // Alias por compatibilidad: "editorial" fue el nombre original de PRO antes de renombrarlo.
        // Negocios que ya lo guardaron con ese valor no deben romperse al leerlo.
        if ("editorial".equalsIgnoreCase(value)) {
            return PRO;
        }
        for (CatalogLayout catalogLayout : values()) {
            if (catalogLayout.value.equalsIgnoreCase(value)) {
                return catalogLayout;
            }
        }
        throw new IllegalArgumentException("CatalogLayout inválido: " + value);
    }
}
