package com.zam.vendy.dtos.negociotexto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NegocioTextoRequest {

    @NotBlank(message = "El texto es obligatorio")
    private String texto;
}
