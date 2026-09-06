package com.zam.vendy.dtos.seccion;

import com.zam.vendy.entities.Seccion;

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
public class SeccionResponse {

    private Long id;
    private String nombre;
    private Integer orden;
    private Long pestanaId;

    public static SeccionResponse from(Seccion seccion) {
        return SeccionResponse.builder()
                .id(seccion.getId())
                .nombre(seccion.getNombre())
                .orden(seccion.getOrden())
                .pestanaId(seccion.getPestana() != null ? seccion.getPestana().getId() : null)
                .build();
    }
}
