package com.zam.vendy.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zam.vendy.entities.ConsultaWhatsapp;

public interface ConsultaWhatsappRepository extends JpaRepository<ConsultaWhatsapp, Long> {

    long countByNegocio_Id(Long negocioId);

    List<ConsultaWhatsapp> findTop10ByNegocio_IdOrderByCreadoEnDesc(Long negocioId);
}
