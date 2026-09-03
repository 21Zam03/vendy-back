package com.zam.vendy.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.zam.vendy.entities.VisitaCatalogo;

public interface VisitaCatalogoRepository extends JpaRepository<VisitaCatalogo, Long> {

    List<VisitaCatalogo> findByNegocio_IdAndFechaBetweenOrderByFechaAsc(
            Long negocioId, LocalDate desde, LocalDate hasta);

    @Query("SELECT COALESCE(SUM(v.cantidad), 0) FROM VisitaCatalogo v WHERE v.negocio.id = :negocioId")
    long sumCantidadByNegocioId(@Param("negocioId") Long negocioId);

    @Modifying
    @Query("UPDATE VisitaCatalogo v SET v.cantidad = v.cantidad + 1 "
            + "WHERE v.negocio.id = :negocioId AND v.fecha = :fecha")
    int incrementarCantidad(@Param("negocioId") Long negocioId, @Param("fecha") LocalDate fecha);
}
