package com.zam.vendy.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zam.vendy.entities.Membresia;

public interface MembresiaRepository extends JpaRepository<Membresia, Long> {

    Optional<Membresia> findByClave(String clave);
}
