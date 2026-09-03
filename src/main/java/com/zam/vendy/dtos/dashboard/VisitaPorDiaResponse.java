package com.zam.vendy.dtos.dashboard;

import java.time.LocalDate;

public record VisitaPorDiaResponse(LocalDate fecha, int cantidad) {
}
