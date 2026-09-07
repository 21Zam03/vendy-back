package com.zam.vendy.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zam.vendy.entities.Membresia;
import com.zam.vendy.entities.Negocio;
import com.zam.vendy.entities.Suscripcion;
import com.zam.vendy.entities.enums.Plan;
import com.zam.vendy.exceptions.LimitePlanExcedidoException;
import com.zam.vendy.repositories.MembresiaRepository;
import com.zam.vendy.repositories.SuscripcionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SuscripcionService {

    private final SuscripcionRepository suscripcionRepository;
    private final MembresiaRepository membresiaRepository;

    // Resuelve la Suscripcion activa de un negocio (con su Membresia y fecha de alta).
    // Autocura negocios de antes de que existiera esta tabla (cuando el plan vivía en la
    // columna Negocio.plan): si todavía no tienen ninguna Suscripcion, se les crea una que
    // preserva ese plan viejo, en vez de reiniciarlos a Gratis. Corre una sola vez por
    // negocio — la próxima vez ya encuentra la Suscripcion y no vuelve a crear nada.
    @Transactional
    public Suscripcion obtenerOAsegurarSuscripcionActiva(Negocio negocio) {
        return suscripcionRepository.findActivaByNegocio_Id(negocio.getId())
                .orElseGet(() -> migrarDesdeColumnaVieja(negocio));
    }

    @Transactional
    public Membresia obtenerOAsegurarMembresiaActiva(Negocio negocio) {
        return obtenerOAsegurarSuscripcionActiva(negocio).getMembresia();
    }

    private Suscripcion migrarDesdeColumnaVieja(Negocio negocio) {
        String clave = negocio.getPlan() != null ? negocio.getPlan().name() : Plan.GRATIS.name();
        Membresia membresia = membresiaRepository.findByClave(clave)
                .or(() -> membresiaRepository.findByClave(Plan.GRATIS.name()))
                .orElseThrow(() -> new IllegalStateException(
                        "No se encontraron las membresías base — revisa MembresiaSeeder"));

        return suscripcionRepository.save(Suscripcion.builder()
                .negocio(negocio)
                .membresia(membresia)
                .activa(true)
                .build());
    }

    // Alta de un negocio recién creado: siempre arranca en Gratis (nunca se salta este
    // paso ni se le asigna otro plan de entrada — cambiar de plan es un paso manual
    // aparte, ver SolicitudMembresia).
    @Transactional
    public void asegurarSuscripcionInicial(Negocio negocio) {
        if (suscripcionRepository.existsByNegocio_Id(negocio.getId())) {
            return;
        }

        Membresia gratis = membresiaRepository.findByClave(Plan.GRATIS.name())
                .orElseThrow(() -> new IllegalStateException(
                        "No se encontraron las membresías base — revisa MembresiaSeeder"));

        suscripcionRepository.save(Suscripcion.builder()
                .negocio(negocio)
                .membresia(gratis)
                .activa(true)
                .build());
    }

    @Transactional
    public int nivelActual(Negocio negocio) {
        return obtenerOAsegurarMembresiaActiva(negocio).getNivel();
    }

    // Para respuestas de API que todavía exponen el plan como el enum Plan (ver
    // NegocioResponse) — Membresia.clave siempre coincide con el nombre de un valor de
    // Plan (ver MembresiaSeeder), así que esta conversión nunca debería fallar.
    @Transactional
    public Plan planActual(Negocio negocio) {
        return Plan.valueOf(obtenerOAsegurarMembresiaActiva(negocio).getClave());
    }

    // Guardia reusable para módulos/funciones que un plan no incluye (Colecciones,
    // Estadísticas, plantillas de catálogo, límite de productos).
    @Transactional
    public void requiereNivel(Negocio negocio, int nivelMinimo) {
        if (nivelActual(negocio) < nivelMinimo) {
            throw new LimitePlanExcedidoException(
                    "Esta función no está disponible en tu plan actual. Mejora tu plan para usarla.");
        }
    }
}
