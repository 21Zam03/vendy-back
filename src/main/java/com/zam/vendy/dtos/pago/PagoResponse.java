package com.zam.vendy.dtos.pago;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.zam.vendy.entities.Pago;

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
public class PagoResponse {

    private Long id;
    private String membresiaNombre;
    private BigDecimal monto;
    private LocalDate fechaPago;
    private String metodo;
    private String nota;

    public static PagoResponse from(Pago pago) {
        return PagoResponse.builder()
                .id(pago.getId())
                .membresiaNombre(pago.getMembresia() != null ? pago.getMembresia().getNombre() : null)
                .monto(pago.getMonto())
                .fechaPago(pago.getFechaPago())
                .metodo(pago.getMetodo())
                .nota(pago.getNota())
                .build();
    }
}
