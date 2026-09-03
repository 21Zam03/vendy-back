package com.zam.vendy.dtos.dashboard;

import java.time.LocalDateTime;

public record ConsultaRecienteResponse(
        Long id,
        Long productoId,
        String productoNombre,
        LocalDateTime creadoEn) {
}
