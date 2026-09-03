package com.zam.vendy.dtos.enlace;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EnlaceRequest {

    @NotBlank(message = "El texto del enlace es obligatorio")
    private String label;

    @NotBlank(message = "La URL es obligatoria")
    private String url;
}
