package com.zam.vendy.dtos.categoria;

import com.zam.vendy.entities.Categoria;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoriaResponse {

    private Long id;
    private String nombre;
    private String emoji;

    public static CategoriaResponse from(Categoria categoria) {
        return CategoriaResponse.builder()
                .id(categoria.getId())
                .nombre(categoria.getNombre())
                .emoji(categoria.getEmoji())
                .build();
    }
}
