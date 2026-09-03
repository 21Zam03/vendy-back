package com.zam.vendy.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zam.vendy.entities.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    List<Categoria> findByNegocio_IdOrderByNombreAsc(Long negocioId);

    Optional<Categoria> findByIdAndNegocio_Id(Long id, Long negocioId);
}
