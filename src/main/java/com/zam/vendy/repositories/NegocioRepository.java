package com.zam.vendy.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

import com.zam.vendy.entities.Negocio;

public interface NegocioRepository extends JpaRepository<Negocio, Long> {

    Optional<Negocio> findByUsuario_IdUsuario(Integer idUsuario);

    Optional<Negocio> findBySlug(String slug);

    boolean existsBySlug(String slug);

    boolean existsByIdAndUsuario_IdUsuario(Long id, Integer idUsuario);

    // Bloquea la fila del negocio hasta que termine la transacción actual: usado por
    // PestanaService.asegurarSinHuerfanas para que dos requests concurrentes del mismo
    // negocio (ej. dos pestañas del navegador abiertas a la vez) no lean el catálogo "sin
    // Inicio todavía" al mismo tiempo y cada una cree su propia pestaña "Inicio" duplicada
    // — la segunda espera a que la primera termine y ya la encuentra creada.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select n from Negocio n where n.id = :id")
    Optional<Negocio> lockById(@Param("id") Long id);
}
