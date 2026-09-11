package com.zam.vendy.dtos.pestana;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PestanaActivaRequest {

    @NotNull(message = "activa es obligatorio")
    private Boolean activa;
}
