package com.zam.vendy.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zam.vendy.dtos.solicitudregistro.SolicitudRegistroRequest;
import com.zam.vendy.entities.SolicitudRegistro;
import com.zam.vendy.repositories.SolicitudRegistroRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SolicitudRegistroService {

    private final SolicitudRegistroRepository solicitudRegistroRepository;

    @Transactional
    public void crear(SolicitudRegistroRequest request) {
        SolicitudRegistro solicitud = SolicitudRegistro.builder()
                .nombre(request.getNombre())
                .nombreNegocio(request.getNombreNegocio())
                .email(request.getEmail())
                .telefono(request.getTelefono())
                .build();

        solicitudRegistroRepository.save(solicitud);
    }
}
