package com.zam.vendy.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.zam.vendy.entities.enums.Plantilla;
import com.zam.vendy.repositories.NegocioBannerRepository;
import com.zam.vendy.repositories.NegocioTextoRepository;

import lombok.RequiredArgsConstructor;

// Corrige datos de antes de que NegocioBanner/NegocioTexto guardaran el progreso por
// plantilla (ver esas entidades): en ese momento Accesorios todavía no tenía su propio
// "Home", así que cualquier slot "home-*" guardado sin plantilla asignada solo pudo
// haberse creado con Moda activa — es la única explicación posible para esos datos.
// Sin esto, esas fotos/títulos viejos quedaban marcados como "globales" (plantilla null)
// y se veían en cualquier plantilla, incluida Accesorios, en vez de solo en Moda.
// Idempotente: la segunda vez que corre no encuentra filas para actualizar.
@Component
@RequiredArgsConstructor
public class LegadoHomeSlotsMigrador implements CommandLineRunner {

    private final NegocioBannerRepository negocioBannerRepository;
    private final NegocioTextoRepository negocioTextoRepository;

    @Override
    @Transactional
    public void run(String... args) {
        negocioBannerRepository.asignarPlantillaALosDeHomeSinPlantilla(Plantilla.MODA);
        negocioTextoRepository.asignarPlantillaALosDeHomeSinPlantilla(Plantilla.MODA);
    }
}
