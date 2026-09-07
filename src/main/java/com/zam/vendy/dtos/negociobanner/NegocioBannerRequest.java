package com.zam.vendy.dtos.negociobanner;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NegocioBannerRequest {

    @NotBlank(message = "La URL de la imagen es obligatoria")
    private String imagenUrl;
}
