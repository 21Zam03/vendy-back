package com.zam.vendy.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zam.vendy.dtos.seccion.SeccionRequest;
import com.zam.vendy.entities.Negocio;
import com.zam.vendy.entities.Pestana;
import com.zam.vendy.entities.Seccion;
import com.zam.vendy.exceptions.ResourceNotFoundException;
import com.zam.vendy.repositories.ProductoRepository;
import com.zam.vendy.repositories.SeccionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SeccionService {

    private final SeccionRepository seccionRepository;
    private final ProductoRepository productoRepository;
    private final NegocioService negocioService;
    private final PestanaService pestanaService;

    @Transactional
    public List<Seccion> listar(Integer idUsuario) {
        Negocio negocio = negocioService.obtenerPorUsuario(idUsuario);
        pestanaService.asegurarSinHuerfanas(negocio);
        return seccionRepository.findByNegocio_IdOrderByOrdenAscIdAsc(negocio.getId());
    }

    @Transactional
    public Seccion crear(Integer idUsuario, SeccionRequest request) {
        Negocio negocio = negocioService.obtenerPorUsuario(idUsuario);
        Pestana pestana = pestanaService.obtenerPropia(idUsuario, request.getPestanaId());
        int siguienteOrden = seccionRepository.findByNegocio_IdOrderByOrdenAscIdAsc(negocio.getId()).size();

        Seccion seccion = Seccion.builder()
                .negocio(negocio)
                .pestana(pestana)
                .nombre(request.getNombre())
                .orden(siguienteOrden)
                .build();

        return seccionRepository.save(seccion);
    }

    @Transactional
    public Seccion actualizar(Integer idUsuario, Long seccionId, SeccionRequest request) {
        Seccion seccion = obtenerPropia(idUsuario, seccionId);
        Pestana pestana = pestanaService.obtenerPropia(idUsuario, request.getPestanaId());
        seccion.setNombre(request.getNombre());
        seccion.setPestana(pestana);
        return seccion;
    }

    @Transactional
    public void eliminar(Integer idUsuario, Long seccionId) {
        Seccion seccion = obtenerPropia(idUsuario, seccionId);

        productoRepository.desasociarSeccion(seccion.getId());
        seccionRepository.delete(seccion);
    }

    @Transactional
    public List<Seccion> reordenar(Integer idUsuario, List<Long> idsEnOrden) {
        Negocio negocio = negocioService.obtenerPorUsuario(idUsuario);
        List<Seccion> propias = seccionRepository.findByNegocio_IdOrderByOrdenAscIdAsc(negocio.getId());

        for (int i = 0; i < idsEnOrden.size(); i++) {
            final Long id = idsEnOrden.get(i);
            final int orden = i;
            propias.stream()
                    .filter(s -> s.getId().equals(id))
                    .findFirst()
                    .ifPresent(s -> s.setOrden(orden));
        }

        return seccionRepository.findByNegocio_IdOrderByOrdenAscIdAsc(negocio.getId());
    }

    private Seccion obtenerPropia(Integer idUsuario, Long seccionId) {
        Negocio negocio = negocioService.obtenerPorUsuario(idUsuario);
        return seccionRepository.findByIdAndNegocio_Id(seccionId, negocio.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Sección no encontrada"));
    }
}
