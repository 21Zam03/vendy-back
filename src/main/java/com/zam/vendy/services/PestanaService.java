package com.zam.vendy.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zam.vendy.dtos.pestana.PestanaRequest;
import com.zam.vendy.entities.Negocio;
import com.zam.vendy.entities.Pestana;
import com.zam.vendy.entities.Seccion;
import com.zam.vendy.exceptions.PestanaNoVaciaException;
import com.zam.vendy.exceptions.ResourceNotFoundException;
import com.zam.vendy.repositories.PestanaRepository;
import com.zam.vendy.repositories.SeccionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PestanaService {

    private final PestanaRepository pestanaRepository;
    private final SeccionRepository seccionRepository;
    private final NegocioService negocioService;

    @Transactional
    public List<Pestana> listar(Integer idUsuario) {
        Negocio negocio = negocioService.obtenerPorUsuario(idUsuario);
        asegurarSinHuerfanas(negocio);
        return pestanaRepository.findByNegocio_IdOrderByOrdenAscIdAsc(negocio.getId());
    }

    @Transactional
    public Pestana crear(Integer idUsuario, PestanaRequest request) {
        Negocio negocio = negocioService.obtenerPorUsuario(idUsuario);
        int siguienteOrden = pestanaRepository.findByNegocio_IdOrderByOrdenAscIdAsc(negocio.getId()).size();

        Pestana pestana = Pestana.builder()
                .negocio(negocio)
                .nombre(request.getNombre())
                .orden(siguienteOrden)
                .build();

        return pestanaRepository.save(pestana);
    }

    @Transactional
    public Pestana actualizar(Integer idUsuario, Long pestanaId, PestanaRequest request) {
        Pestana pestana = obtenerPropia(idUsuario, pestanaId);
        pestana.setNombre(request.getNombre());
        return pestana;
    }

    @Transactional
    public void eliminar(Integer idUsuario, Long pestanaId) {
        Pestana pestana = obtenerPropia(idUsuario, pestanaId);

        if (!seccionRepository.findByPestana_IdOrderByOrdenAscIdAsc(pestana.getId()).isEmpty()) {
            throw new PestanaNoVaciaException(pestana.getNombre());
        }

        pestanaRepository.delete(pestana);
    }

    @Transactional
    public List<Pestana> reordenar(Integer idUsuario, List<Long> idsEnOrden) {
        Negocio negocio = negocioService.obtenerPorUsuario(idUsuario);
        List<Pestana> propias = pestanaRepository.findByNegocio_IdOrderByOrdenAscIdAsc(negocio.getId());

        for (int i = 0; i < idsEnOrden.size(); i++) {
            final Long id = idsEnOrden.get(i);
            final int orden = i;
            propias.stream()
                    .filter(p -> p.getId().equals(id))
                    .findFirst()
                    .ifPresent(p -> p.setOrden(orden));
        }

        return pestanaRepository.findByNegocio_IdOrderByOrdenAscIdAsc(negocio.getId());
    }

    public Pestana obtenerPropia(Integer idUsuario, Long pestanaId) {
        Negocio negocio = negocioService.obtenerPorUsuario(idUsuario);
        return pestanaRepository.findByIdAndNegocio_Id(pestanaId, negocio.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Pestaña no encontrada"));
    }

    // Autocura negocios de antes de que existieran las pestañas: cualquier sección sin
    // pestaña se agrupa en una "General" recién creada, para que la jerarquía pestaña →
    // sección quede completa sin tener que migrar datos a mano.
    @Transactional
    public void asegurarSinHuerfanas(Negocio negocio) {
        List<Seccion> huerfanas = seccionRepository.findByNegocio_IdAndPestanaIsNull(negocio.getId());
        if (huerfanas.isEmpty()) {
            return;
        }

        int siguienteOrden = pestanaRepository.findByNegocio_IdOrderByOrdenAscIdAsc(negocio.getId()).size();
        Pestana general = pestanaRepository.save(Pestana.builder()
                .negocio(negocio)
                .nombre("General")
                .orden(siguienteOrden)
                .build());

        huerfanas.forEach(seccion -> seccion.setPestana(general));
    }
}
