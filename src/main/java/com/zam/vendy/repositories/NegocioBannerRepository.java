package com.zam.vendy.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zam.vendy.entities.NegocioBanner;

public interface NegocioBannerRepository extends JpaRepository<NegocioBanner, Long> {

    List<NegocioBanner> findByNegocio_Id(Long negocioId);

    Optional<NegocioBanner> findByNegocio_IdAndSlot(Long negocioId, String slot);

    void deleteByNegocio_IdAndSlot(Long negocioId, String slot);
}
