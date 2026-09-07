package com.zam.vendy.dtos.solicitudmembresia;

import com.zam.vendy.entities.enums.Plan;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudMembresiaRequest {

    @NotNull(message = "El plan es obligatorio")
    private Plan plan;
}
