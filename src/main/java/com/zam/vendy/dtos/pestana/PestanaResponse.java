package com.zam.vendy.dtos.pestana;

import com.zam.vendy.entities.Pestana;

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
public class PestanaResponse {

    private Long id;
    private String nombre;
    private Integer orden;

    public static PestanaResponse from(Pestana pestana) {
        return PestanaResponse.builder()
                .id(pestana.getId())
                .nombre(pestana.getNombre())
                .orden(pestana.getOrden())
                .build();
    }
}
