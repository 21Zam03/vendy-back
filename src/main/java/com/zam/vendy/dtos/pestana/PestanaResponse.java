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
    private Boolean esHome;
    private Boolean esGeneral;
    private Boolean activa;

    public static PestanaResponse from(Pestana pestana) {
        return PestanaResponse.builder()
                .id(pestana.getId())
                .nombre(pestana.getNombre())
                .orden(pestana.getOrden())
                .esHome(Boolean.TRUE.equals(pestana.getEsHome()))
                .esGeneral(Boolean.TRUE.equals(pestana.getEsGeneral()))
                // null (pestañas de antes de este campo) se manda como activa=true: nunca
                // deben desaparecer solas del catálogo por una migración de columna.
                .activa(!Boolean.FALSE.equals(pestana.getActiva()))
                .build();
    }
}
