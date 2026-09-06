package com.zam.vendy.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zam.vendy.entities.Pestana;

public interface PestanaRepository extends JpaRepository<Pestana, Long> {

    List<Pestana> findByNegocio_IdOrderByOrdenAscIdAsc(Long negocioId);

    Optional<Pestana> findByIdAndNegocio_Id(Long id, Long negocioId);
}
