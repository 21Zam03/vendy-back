package com.zam.vendy.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zam.vendy.dtos.enlace.EnlaceRequest;
import com.zam.vendy.entities.EnlaceNegocio;
import com.zam.vendy.entities.Negocio;
import com.zam.vendy.exceptions.ResourceNotFoundException;
import com.zam.vendy.repositories.EnlaceNegocioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EnlaceService {

    private final EnlaceNegocioRepository enlaceNegocioRepository;
    private final NegocioService negocioService;

    @Transactional(readOnly = true)
    public List<EnlaceNegocio> listar(Integer idUsuario) {
        Negocio negocio = negocioService.obtenerPorUsuario(idUsuario);
        return enlaceNegocioRepository.findByNegocio_IdOrderByOrdenAscIdAsc(negocio.getId());
    }

    @Transactional
    public EnlaceNegocio crear(Integer idUsuario, EnlaceRequest request) {
        Negocio negocio = negocioService.obtenerPorUsuario(idUsuario);

        EnlaceNegocio enlace = EnlaceNegocio.builder()
                .negocio(negocio)
                .label(request.getLabel())
                .url(request.getUrl())
                .build();

        return enlaceNegocioRepository.save(enlace);
    }

    @Transactional
    public EnlaceNegocio actualizar(Integer idUsuario, Long enlaceId, EnlaceRequest request) {
        EnlaceNegocio enlace = obtenerPropio(idUsuario, enlaceId);

        enlace.setLabel(request.getLabel());
        enlace.setUrl(request.getUrl());

        return enlace;
    }

    @Transactional
    public void eliminar(Integer idUsuario, Long enlaceId) {
        EnlaceNegocio enlace = obtenerPropio(idUsuario, enlaceId);
        enlaceNegocioRepository.delete(enlace);
    }

    private EnlaceNegocio obtenerPropio(Integer idUsuario, Long enlaceId) {
        Negocio negocio = negocioService.obtenerPorUsuario(idUsuario);
        return enlaceNegocioRepository.findByIdAndNegocio_Id(enlaceId, negocio.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Enlace no encontrado"));
    }
}
