package com.zam.vendy.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.zam.vendy.entities.NegocioTexto;
import com.zam.vendy.entities.enums.Plantilla;

public interface NegocioTextoRepository extends JpaRepository<NegocioTexto, Long> {

    // Ver NegocioBannerRepository.findVisiblesPorNegocio: mismo criterio (plantilla activa
    // + los globales) y mismo orden (globales primero, para que el toMap de quien llama
    // se quede con la fila de la plantilla si existen las dos para el mismo slot).
    @Query("""
            SELECT t FROM NegocioTexto t
            WHERE t.negocio.id = :negocioId
              AND (t.plantilla IS NULL OR t.plantilla = :plantilla)
            ORDER BY CASE WHEN t.plantilla IS NULL THEN 0 ELSE 1 END
            """)
    List<NegocioTexto> findVisiblesPorNegocio(@Param("negocioId") Long negocioId, @Param("plantilla") Plantilla plantilla);

    Optional<NegocioTexto> findByNegocio_IdAndSlotAndPlantilla(Long negocioId, String slot, Plantilla plantilla);

    void deleteByNegocio_IdAndSlotAndPlantilla(Long negocioId, String slot, Plantilla plantilla);

    // Ver NegocioBannerRepository.asignarPlantillaALosDeHomeSinPlantilla: mismo motivo.
    @Modifying
    @Query("UPDATE NegocioTexto t SET t.plantilla = :plantilla WHERE t.plantilla IS NULL AND t.slot LIKE 'home-%'")
    int asignarPlantillaALosDeHomeSinPlantilla(@Param("plantilla") Plantilla plantilla);
}
