package com.zam.vendy.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.zam.vendy.entities.NegocioBanner;
import com.zam.vendy.entities.enums.Plantilla;

public interface NegocioBannerRepository extends JpaRepository<NegocioBanner, Long> {

    // Los que aplican al negocio ahora mismo: los de la plantilla activa (progreso propio
    // de esa plantilla) más los que no son de ninguna plantilla en particular (ej. el
    // topbar, o banners de antes de que existiera esta columna). Si "plantilla" es null
    // (sin plantilla elegida, catálogo general), esto devuelve solo los globales — no hay
    // ningún "Home" que mostrar los de plantilla.
    // Orden: los globales (null) primero — si un mismo slot tiene una fila vieja global Y
    // una nueva ya propia de esta plantilla, el toMap de quien llama se queda con la
    // última (la de la plantilla), no con la global.
    @Query("""
            SELECT b FROM NegocioBanner b
            WHERE b.negocio.id = :negocioId
              AND (b.plantilla IS NULL OR b.plantilla = :plantilla)
            ORDER BY CASE WHEN b.plantilla IS NULL THEN 0 ELSE 1 END
            """)
    List<NegocioBanner> findVisiblesPorNegocio(@Param("negocioId") Long negocioId, @Param("plantilla") Plantilla plantilla);

    Optional<NegocioBanner> findByNegocio_IdAndSlotAndPlantilla(Long negocioId, String slot, Plantilla plantilla);

    void deleteByNegocio_IdAndSlotAndPlantilla(Long negocioId, String slot, Plantilla plantilla);

    // Migración de datos viejos (ver LegadoHomeSlotsMigrador): antes de que Accesorios
    // tuviera su propio Home, cualquier slot "home-*" sin plantilla asignada solo pudo
    // haberse creado con Moda activa — era la única plantilla con Home en ese momento.
    @Modifying
    @Query("UPDATE NegocioBanner b SET b.plantilla = :plantilla WHERE b.plantilla IS NULL AND b.slot LIKE 'home-%'")
    int asignarPlantillaALosDeHomeSinPlantilla(@Param("plantilla") Plantilla plantilla);
}
