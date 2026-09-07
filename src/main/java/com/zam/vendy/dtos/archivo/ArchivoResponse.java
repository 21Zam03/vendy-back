package com.zam.vendy.dtos.archivo;

import java.time.LocalDateTime;

import com.zam.vendy.entities.ArchivoNegocio;

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
public class ArchivoResponse {

    private Long id;
    private String url;
    private String nombre;
    private LocalDateTime createdAt;

    public static ArchivoResponse from(ArchivoNegocio archivo) {
        return ArchivoResponse.builder()
                .id(archivo.getId())
                .url(archivo.getUrl())
                .nombre(archivo.getNombre())
                .createdAt(archivo.getCreatedAt())
                .build();
    }
}
