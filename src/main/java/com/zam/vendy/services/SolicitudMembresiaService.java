package com.zam.vendy.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zam.vendy.entities.Negocio;
import com.zam.vendy.entities.SolicitudMembresia;
import com.zam.vendy.entities.enums.Plan;
import com.zam.vendy.exceptions.LimitePlanExcedidoException;
import com.zam.vendy.repositories.SolicitudMembresiaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SolicitudMembresiaService {

    private final SolicitudMembresiaRepository solicitudMembresiaRepository;
    private final NegocioService negocioService;
    private final SuscripcionService suscripcionService;

    @Transactional
    public void crear(Integer idUsuario, Plan planSolicitado) {
        Negocio negocio = negocioService.obtenerPorUsuario(idUsuario);
        Plan planActual = suscripcionService.planActual(negocio);

        // Validado contra la Suscripcion real, no contra lo que el frontend crea que
        // tiene — evita pedidos duplicados de un plan que ya está activo.
        if (planActual == planSolicitado) {
            throw new LimitePlanExcedidoException("Ya tienes activo el plan que intentas solicitar.");
        }

        SolicitudMembresia solicitud = SolicitudMembresia.builder()
                .negocio(negocio)
                .planActual(planActual)
                .planSolicitado(planSolicitado)
                .build();

        solicitudMembresiaRepository.save(solicitud);
    }
}
