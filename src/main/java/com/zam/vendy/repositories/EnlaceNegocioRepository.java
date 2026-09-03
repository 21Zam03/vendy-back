package com.zam.vendy.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zam.vendy.entities.EnlaceNegocio;

public interface EnlaceNegocioRepository extends JpaRepository<EnlaceNegocio, Long> {

    List<EnlaceNegocio> findByNegocio_IdOrderByOrdenAscIdAsc(Long negocioId);

    Optional<EnlaceNegocio> findByIdAndNegocio_Id(Long id, Long negocioId);
}
