package com.zam.vendy.services;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zam.vendy.entities.Negocio;
import com.zam.vendy.entities.NegocioTexto;
import com.zam.vendy.repositories.NegocioTextoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NegocioTextoService {

    private final NegocioTextoRepository negocioTextoRepository;
    private final NegocioService negocioService;

    @Transactional(readOnly = true)
    public Map<String, String> listarPropios(Integer idUsuario) {
        Negocio negocio = negocioService.obtenerPorUsuario(idUsuario);
        return listarPorNegocio(negocio.getId());
    }

    @Transactional(readOnly = true)
    public Map<String, String> listarPorNegocio(Long negocioId) {
        return negocioTextoRepository.findByNegocio_Id(negocioId).stream()
                .collect(Collectors.toMap(NegocioTexto::getSlot, NegocioTexto::getTexto));
    }

    @Transactional
    public void guardar(Integer idUsuario, String slot, String texto) {
        Negocio negocio = negocioService.obtenerPorUsuario(idUsuario);
        NegocioTexto item = negocioTextoRepository.findByNegocio_IdAndSlot(negocio.getId(), slot)
                .orElseGet(() -> NegocioTexto.builder().negocio(negocio).slot(slot).build());
        item.setTexto(texto);
        negocioTextoRepository.save(item);
    }

    @Transactional
    public void eliminar(Integer idUsuario, String slot) {
        Negocio negocio = negocioService.obtenerPorUsuario(idUsuario);
        negocioTextoRepository.deleteByNegocio_IdAndSlot(negocio.getId(), slot);
    }
}
