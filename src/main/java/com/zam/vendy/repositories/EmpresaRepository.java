package com.zam.vendy.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zam.vendy.entities.Empresa;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
}
