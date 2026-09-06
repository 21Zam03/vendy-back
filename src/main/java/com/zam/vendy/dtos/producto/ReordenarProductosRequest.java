package com.zam.vendy.dtos.producto;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReordenarProductosRequest {

    // null = reordenar los productos sin sección asignada.
    private Long seccionId;

    @NotEmpty(message = "idsEnOrden no puede estar vacío")
    private List<Long> idsEnOrden;
}
