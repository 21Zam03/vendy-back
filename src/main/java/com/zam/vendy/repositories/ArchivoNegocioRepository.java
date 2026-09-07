package com.zam.vendy.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zam.vendy.entities.ArchivoNegocio;

public interface ArchivoNegocioRepository extends JpaRepository<ArchivoNegocio, Long> {

    List<ArchivoNegocio> findByNegocio_IdOrderByCreatedAtDesc(Long negocioId);

    Optional<ArchivoNegocio> findByIdAndNegocio_Id(Long id, Long negocioId);
}
