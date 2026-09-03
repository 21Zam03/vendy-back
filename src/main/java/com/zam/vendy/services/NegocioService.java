package com.zam.vendy.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zam.vendy.dtos.negocio.MetodoPagoRequest;
import com.zam.vendy.dtos.negocio.NegocioUpdateRequest;
import com.zam.vendy.entities.Negocio;
import com.zam.vendy.entities.embeddable.Apariencia;
import com.zam.vendy.entities.embeddable.MetodoPago;
import com.zam.vendy.entities.enums.CatalogLayout;
import com.zam.vendy.entities.embeddable.RedesSociales;
import com.zam.vendy.exceptions.ResourceNotFoundException;
import com.zam.vendy.exceptions.SlugYaExisteException;
import com.zam.vendy.repositories.NegocioRepository;
import com.zam.vendy.repositories.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NegocioService {

    private final NegocioRepository negocioRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public Negocio obtenerPorUsuario(Integer idUsuario) {
        return negocioRepository.findByUsuario_IdUsuario(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "El usuario actual todavía no tiene un negocio configurado"));
    }

    @Transactional
    public Negocio guardar(Integer idUsuario, NegocioUpdateRequest request) {
        Optional<Negocio> conMismoSlug = negocioRepository.findBySlug(request.getSlug());
        if (conMismoSlug.isPresent() && !conMismoSlug.get().getUsuario().getIdUsuario().equals(idUsuario)) {
            throw new SlugYaExisteException(request.getSlug());
        }

        Negocio negocio = negocioRepository.findByUsuario_IdUsuario(idUsuario)
                .orElseGet(() -> Negocio.builder()
                        .usuario(usuarioRepository.getReferenceById(idUsuario))
                        .build());

        negocio.setNombre(request.getNombre());
        negocio.setSlug(request.getSlug());
        negocio.setDescripcion(request.getDescripcion());
        negocio.setWhatsapp(request.getWhatsapp());
        negocio.setUbicacion(request.getUbicacion());
        negocio.setLogoInitials(request.getLogoInitials());
        negocio.setLogoUrl(request.getLogoUrl());

        negocio.setRedesSociales(RedesSociales.builder()
                .instagram(request.getInstagram())
                .tiktok(request.getTiktok())
                .facebook(request.getFacebook())
                .build());

        negocio.setApariencia(Apariencia.builder()
                .accentColor(request.getAccentColor())
                .background(request.getBackground())
                .font(request.getFont())
                .radius(request.getRadius())
                .cover(request.getCover())
                .catalogLayout(request.getCatalogLayout() != null ? request.getCatalogLayout() : CatalogLayout.GRID)
                .build());

        negocio.setMetodosPago(mapearMetodosPago(request.getMetodosPago()));

        return negocioRepository.save(negocio);
    }

    // Importante: Hibernate necesita poder mutar (limpiar/rellenar) esta colección al
    // hacer merge del negocio, así que tiene que devolver una lista mutable, nunca
    // List.of()/.toList() (inmutables) o el guardado revienta con UnsupportedOperationException.
    private List<MetodoPago> mapearMetodosPago(List<MetodoPagoRequest> metodosPago) {
        if (metodosPago == null) {
            return new ArrayList<>();
        }
        return metodosPago.stream()
                .map(m -> MetodoPago.builder().tipo(m.getTipo()).detalle(m.getDetalle()).build())
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
