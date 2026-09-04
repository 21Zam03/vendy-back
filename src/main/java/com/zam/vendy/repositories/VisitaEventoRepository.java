package com.zam.vendy.repositories;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zam.vendy.entities.VisitaEvento;

public interface VisitaEventoRepository extends JpaRepository<VisitaEvento, Long> {

    boolean existsByNegocio_IdAndVisitorIdAndFecha(Long negocioId, String visitorId, LocalDate fecha);
}
