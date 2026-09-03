package com.zam.vendy.dtos.negocio;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MetodoPagoRequest {

    @NotBlank(message = "El tipo de método de pago es obligatorio")
    private String tipo;

    private String detalle;
}
