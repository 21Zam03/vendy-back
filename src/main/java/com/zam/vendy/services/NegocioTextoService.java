package com.zam.vendy.services;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zam.vendy.entities.Negocio;
import com.zam.vendy.entities.NegocioTexto;
import com.zam.vendy.entities.enums.Plantilla;
import com.zam.vendy.repositories.NegocioRepository;
import com.zam.vendy.repositories.NegocioTextoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NegocioTextoService {

    private final NegocioTextoRepository negocioTextoRepository;
    private final NegocioService negocioService;
    private final NegocioRepository negocioRepository;

    @Transactional(readOnly = true)
    public Map<String, String> listarPropios(Integer idUsuario) {
        Negocio negocio = negocioService.obtenerPorUsuario(idUsuario);
        return listarPorNegocio(negocio);
    }

    @Transactional(readOnly = true)
    public Map<String, String> listarPorNegocio(Negocio negocio) {
        return negocioTextoRepository.findVisiblesPorNegocio(negocio.getId(), negocio.getApariencia().getPlantilla()).stream()
                .collect(Collectors.toMap(NegocioTexto::getSlot, NegocioTexto::getTexto, (anterior, nuevo) -> nuevo));
    }

    @Transactional
    public void guardar(Integer idUsuario, String slot, String texto) {
        Negocio negocio = negocioService.obtenerPorUsuario(idUsuario);
        // Ver el mismo bloqueo en NegocioBannerService: evita el mismo choque contra el
        // UNIQUE (negocio_id, plantilla, slot) con dos requests concurrentes del mismo slot.
        negocioRepository.lockById(negocio.getId());
        Plantilla plantilla = resolverPlantilla(negocio, slot);
        NegocioTexto item = negocioTextoRepository.findByNegocio_IdAndSlotAndPlantilla(negocio.getId(), slot, plantilla)
                .orElseGet(() -> NegocioTexto.builder().negocio(negocio).slot(slot).plantilla(plantilla).build());
        item.setTexto(texto);
        negocioTextoRepository.save(item);
    }

    @Transactional
    public void eliminar(Integer idUsuario, String slot) {
        Negocio negocio = negocioService.obtenerPorUsuario(idUsuario);
        negocioTextoRepository.deleteByNegocio_IdAndSlotAndPlantilla(negocio.getId(), slot, resolverPlantilla(negocio, slot));
    }

    // Ver el mismo helper en NegocioBannerService.
    private Plantilla resolverPlantilla(Negocio negocio, String slot) {
        return slot.startsWith("home-") ? negocio.getApariencia().getPlantilla() : null;
    }
}
