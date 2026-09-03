package com.zam.vendy.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zam.vendy.entities.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    List<Cliente> findByEmpresa_IdEmpresa(Long idEmpresa);
}
