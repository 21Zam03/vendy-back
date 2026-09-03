package com.zam.vendy.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zam.vendy.entities.Seccion;

public interface SeccionRepository extends JpaRepository<Seccion, Long> {

    List<Seccion> findByNegocio_IdOrderByOrdenAscIdAsc(Long negocioId);

    Optional<Seccion> findByIdAndNegocio_Id(Long id, Long negocioId);
}
