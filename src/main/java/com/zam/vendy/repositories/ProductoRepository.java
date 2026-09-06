package com.zam.vendy.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.zam.vendy.entities.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByNegocio_Id(Long negocioId);

    Optional<Producto> findByIdAndNegocio_Id(Long id, Long negocioId);

    List<Producto> findByNegocio_IdAndActivoTrue(Long negocioId);

    List<Producto> findByNegocio_IdAndActivoTrueAndDestacadoTrueOrderByCreatedAtDesc(Long negocioId);

    Optional<Producto> findByIdAndNegocio_IdAndActivoTrue(Long id, Long negocioId);

    long countByNegocio_IdAndActivoTrue(Long negocioId);

    List<Producto> findByNegocio_IdAndSeccion_Id(Long negocioId, Long seccionId);

    List<Producto> findByNegocio_IdAndSeccionIsNull(Long negocioId);

    List<Producto> findByNegocio_IdAndActivoTrueOrderByOrdenAscIdAsc(Long negocioId);

    @Modifying
    @Query("UPDATE Producto p SET p.categoria = null WHERE p.categoria.id = :categoriaId")
    int desasociarCategoria(@Param("categoriaId") Long categoriaId);

    @Modifying
    @Query("UPDATE Producto p SET p.seccion = null WHERE p.seccion.id = :seccionId")
    int desasociarSeccion(@Param("seccionId") Long seccionId);

    @Modifying
    @Query("UPDATE Producto p SET p.vistas = p.vistas + 1 WHERE p.id = :id")
    int incrementarVistas(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Producto p SET p.consultas = p.consultas + 1 WHERE p.id = :id")
    int incrementarConsultas(@Param("id") Long id);
}
