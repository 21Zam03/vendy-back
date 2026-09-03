package com.zam.vendy.dtos.seccion;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SeccionRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
}
