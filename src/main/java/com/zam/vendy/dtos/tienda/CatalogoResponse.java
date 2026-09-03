package com.zam.vendy.dtos.tienda;

import java.util.List;

import com.zam.vendy.dtos.categoria.CategoriaResponse;

public record CatalogoResponse(
        List<CategoriaResponse> categorias,
        List<ProductoPublicoResponse> productos) {
}
