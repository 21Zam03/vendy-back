package com.zam.vendy.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.zam.vendy.entities.Suscripcion;

public interface SuscripcionRepository extends JpaRepository<Suscripcion, Long> {

    @Query("""
            SELECT s FROM Suscripcion s
            JOIN FETCH s.membresia
            WHERE s.negocio.id = :negocioId AND s.activa = true
            """)
    Optional<Suscripcion> findActivaByNegocio_Id(@Param("negocioId") Long negocioId);

    boolean existsByNegocio_Id(Long negocioId);
}
