package com.zam.vendy.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zam.vendy.entities.Pago;

public interface PagoRepository extends JpaRepository<Pago, Long> {

    List<Pago> findByNegocio_IdOrderByFechaPagoDesc(Long negocioId);
}
