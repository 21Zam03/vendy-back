package com.zam.vendy.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zam.vendy.entities.Catalogo;

public interface CatalogoRepository extends JpaRepository<Catalogo, Long> {

    List<Catalogo> findByNegocio_IdOrderByCreatedAtDesc(Long negocioId);

    List<Catalogo> findByNegocio_IdAndActivoTrueOrderByCreatedAtDesc(Long negocioId);

    Optional<Catalogo> findByIdAndNegocio_Id(Long id, Long negocioId);

    Optional<Catalogo> findByNegocio_IdAndSlug(Long negocioId, String slug);

    Optional<Catalogo> findByNegocio_IdAndSlugAndActivoTrue(Long negocioId, String slug);
}
