package com.zam.vendy.services;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zam.vendy.entities.Negocio;
import com.zam.vendy.entities.NegocioBanner;
import com.zam.vendy.repositories.NegocioBannerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NegocioBannerService {

    private final NegocioBannerRepository negocioBannerRepository;
    private final NegocioService negocioService;

    @Transactional(readOnly = true)
    public Map<String, String> listarPropios(Integer idUsuario) {
        Negocio negocio = negocioService.obtenerPorUsuario(idUsuario);
        return listarPorNegocio(negocio.getId());
    }

    @Transactional(readOnly = true)
    public Map<String, String> listarPorNegocio(Long negocioId) {
        return negocioBannerRepository.findByNegocio_Id(negocioId).stream()
                .collect(Collectors.toMap(NegocioBanner::getSlot, NegocioBanner::getImagenUrl));
    }

    @Transactional
    public void guardar(Integer idUsuario, String slot, String imagenUrl) {
        Negocio negocio = negocioService.obtenerPorUsuario(idUsuario);
        NegocioBanner banner = negocioBannerRepository.findByNegocio_IdAndSlot(negocio.getId(), slot)
                .orElseGet(() -> NegocioBanner.builder().negocio(negocio).slot(slot).build());
        banner.setImagenUrl(imagenUrl);
        negocioBannerRepository.save(banner);
    }

    @Transactional
    public void eliminar(Integer idUsuario, String slot) {
        Negocio negocio = negocioService.obtenerPorUsuario(idUsuario);
        negocioBannerRepository.deleteByNegocio_IdAndSlot(negocio.getId(), slot);
    }
}
