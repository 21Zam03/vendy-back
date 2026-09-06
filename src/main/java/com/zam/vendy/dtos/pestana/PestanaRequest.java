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
}
