package com.zam.vendy.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zam.vendy.entities.Negocio;
import com.zam.vendy.entities.Pago;
import com.zam.vendy.repositories.PagoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PagoService {

    private final PagoRepository pagoRepository;
    private final NegocioService negocioService;

    @Transactional(readOnly = true)
    public List<Pago> listar(Integer idUsuario) {
        Negocio negocio = negocioService.obtenerPorUsuario(idUsuario);
        return pagoRepository.findByNegocio_IdOrderByFechaPagoDesc(negocio.getId());
    }
}
