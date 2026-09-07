package com.zam.vendy.dtos.pestana;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PestanaRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    // Solo se usa al crear (ver PestanaService.crear) — actualizar() nunca la toca, así
    // que renombrar una pestaña jamás le quita ni le agrega esta marca.
    private Boolean esHome;
}
