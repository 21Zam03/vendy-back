package com.zam.vendy.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.zam.vendy.entities.Membresia;
import com.zam.vendy.repositories.MembresiaRepository;

import lombok.RequiredArgsConstructor;

// Asegura que las 3 membresías base existan al arrancar — idempotente (no duplica ni
// pisa las que ya existen), así que es seguro que corra en cada arranque del backend.
// Es el único lugar donde se crean filas de Membresia; cambiar límites/nombres se hace
// acá, no a mano en la base de datos.
@Component
@RequiredArgsConstructor
public class MembresiaSeeder implements CommandLineRunner {

    private static final List<Membresia> BASE = List.of(
            Membresia.builder().clave("GRATIS").nombre("Vendy Gratis").nivel(0).limiteProductos(50).build(),
            Membresia.builder().clave("GO").nombre("Vendy Go").nivel(1).limiteProductos(null).build(),
            Membresia.builder().clave("PREMIUM").nombre("Vendy Premium").nivel(2).limiteProductos(null).build());

    private final MembresiaRepository membresiaRepository;

    @Override
    public void run(String... args) {
        for (Membresia base : BASE) {
            if (membresiaRepository.findByClave(base.getClave()).isEmpty()) {
                membresiaRepository.save(base);
            }
        }
    }
}
