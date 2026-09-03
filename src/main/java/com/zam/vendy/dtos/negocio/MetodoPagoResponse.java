package com.zam.vendy.dtos.negocio;

import com.zam.vendy.entities.embeddable.MetodoPago;

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
public class MetodoPagoResponse {

    private String tipo;
    private String detalle;

    public static MetodoPagoResponse from(MetodoPago metodoPago) {
        return MetodoPagoResponse.builder()
                .tipo(metodoPago.getTipo())
                .detalle(metodoPago.getDetalle())
                .build();
    }
}
