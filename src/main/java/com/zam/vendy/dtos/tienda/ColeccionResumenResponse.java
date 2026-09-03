package com.zam.vendy.dtos.tienda;

import com.zam.vendy.entities.Catalogo;

public record ColeccionResumenResponse(String nombre, String slug) {

    public static ColeccionResumenResponse from(Catalogo catalogo) {
        return new ColeccionResumenResponse(catalogo.getNombre(), catalogo.getSlug());
    }
}
