package com.zam.vendy.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zam.vendy.entities.Negocio;

public interface NegocioRepository extends JpaRepository<Negocio, Long> {

    Optional<Negocio> findByUsuario_IdUsuario(Integer idUsuario);

    Optional<Negocio> findBySlug(String slug);

    boolean existsBySlug(String slug);
}
