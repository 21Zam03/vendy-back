package com.zam.vendy.services;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zam.vendy.entities.Negocio;
import com.zam.vendy.entities.NegocioBanner;
import com.zam.vendy.entities.enums.Plantilla;
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
        return listarPorNegocio(negocio);
    }

    @Transactional(readOnly = true)
    public Map<String, String> listarPorNegocio(Negocio negocio) {
        return negocioBannerRepository.findVisiblesPorNegocio(negocio.getId(), negocio.getApariencia().getPlantilla()).stream()
                .collect(Collectors.toMap(NegocioBanner::getSlot, NegocioBanner::getImagenUrl, (anterior, nuevo) -> nuevo));
    }

    @Transactional
    public void guardar(Integer idUsuario, String slot, String imagenUrl) {
        Negocio negocio = negocioService.obtenerPorUsuario(idUsuario);
        Plantilla plantilla = resolverPlantilla(negocio, slot);
        NegocioBanner banner = negocioBannerRepository.findByNegocio_IdAndSlotAndPlantilla(negocio.getId(), slot, plantilla)
                .orElseGet(() -> NegocioBanner.builder().negocio(negocio).slot(slot).plantilla(plantilla).build());
        banner.setImagenUrl(imagenUrl);
        negocioBannerRepository.save(banner);
    }

    @Transactional
    public void eliminar(Integer idUsuario, String slot) {
        Negocio negocio = negocioService.obtenerPorUsuario(idUsuario);
        negocioBannerRepository.deleteByNegocio_IdAndSlotAndPlantilla(negocio.getId(), slot, resolverPlantilla(negocio, slot));
    }

    // Los slots de un "Home" de plantilla (carrusel, mosaico, etc.) guardan su progreso por
    // separado para cada plantilla; el resto (ej. el topbar) se comparte sin importar cuál
    // esté activa — ver el comentario en NegocioBanner.
    private Plantilla resolverPlantilla(Negocio negocio, String slot) {
        return slot.startsWith("home-") ? negocio.getApariencia().getPlantilla() : null;
    }
}
