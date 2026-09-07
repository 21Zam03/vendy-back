package com.zam.vendy.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zam.vendy.entities.NegocioTexto;

public interface NegocioTextoRepository extends JpaRepository<NegocioTexto, Long> {

    List<NegocioTexto> findByNegocio_Id(Long negocioId);

    Optional<NegocioTexto> findByNegocio_IdAndSlot(Long negocioId, String slot);

    void deleteByNegocio_IdAndSlot(Long negocioId, String slot);
}
