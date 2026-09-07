package com.zam.vendy.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zam.vendy.entities.Usuario;
import com.zam.vendy.entities.enums.ProveedorAuth;
import com.zam.vendy.repositories.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GoogleAuthService {

    private final UsuarioRepository usuarioRepository;

    // Resuelve el Usuario para un login con Google: por googleId si ya se logueó antes
    // así; si no, por correo (Google ya verificó que es dueño de ese correo, así que una
    // cuenta local existente con el mismo correo se vincula en vez de duplicarse — desde
    // ahí puede entrar con Google o con su contraseña, lo que prefiera); si tampoco existe
    // por correo, se crea de cero sin contraseña (login exclusivo por Google). Una cuenta
    // sin Negocio todavía (nueva, o vieja sin completar) queda forzada a "Mi negocio" por
    // el guard del router del frontend — no hace falta distinguir acá si es nueva o no.
    @Transactional
    public Usuario procesarLoginGoogle(String googleId, String correo, String nombres, String apellidos) {
        Usuario existentePorGoogle = usuarioRepository.findByGoogleIdWithRolesAndPermisos(googleId).orElse(null);
        if (existentePorGoogle != null) {
            return existentePorGoogle;
        }

        Usuario existentePorCorreo = usuarioRepository.findByCorreoWithRolesAndPermisos(correo).orElse(null);
        if (existentePorCorreo != null) {
            existentePorCorreo.setGoogleId(googleId);
            return usuarioRepository.save(existentePorCorreo);
        }

        Usuario nuevo = Usuario.builder()
                .nombreUsuario(generarNombreUsuarioUnico(correo))
                .correo(correo)
                .nombres(nombres)
                .apellidos(apellidos)
                .googleId(googleId)
                .proveedor(ProveedorAuth.GOOGLE)
                .activo(true)
                .cuentaBloqueada(false)
                .cuentaExpirada(false)
                .credencialesExpiradas(false)
                .reseteoContrasena(false)
                .build();

        return usuarioRepository.save(nuevo);
    }

    // nombreUsuario es único y Google no lo provee — se arma desde la parte local del
    // correo, sumando un sufijo numérico si ya existe (ej. "juan", "juan1", "juan2"...).
    private String generarNombreUsuarioUnico(String correo) {
        String base = correo.substring(0, correo.indexOf('@')).toLowerCase().replaceAll("[^a-z0-9._-]", "");
        if (base.isBlank()) {
            base = "usuario";
        }

        String candidato = base;
        int sufijo = 1;
        while (usuarioRepository.existsByNombreUsuario(candidato)) {
            candidato = base + sufijo;
            sufijo++;
        }
        return candidato;
    }
}
