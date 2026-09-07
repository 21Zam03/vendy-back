package com.zam.vendy.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.zam.vendy.entities.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByNombreUsuario(String nombreUsuario);

    boolean existsByNombreUsuario(String nombreUsuario);

    @Query("""
            SELECT DISTINCT u FROM Usuario u
            LEFT JOIN FETCH u.roles r
            LEFT JOIN FETCH r.permisos
            LEFT JOIN FETCH u.empresa
            WHERE u.nombreUsuario = :nombreUsuario
            """)
    Optional<Usuario> findByNombreUsuarioWithRolesAndPermisos(@Param("nombreUsuario") String nombreUsuario);

    @Query("""
            SELECT DISTINCT u FROM Usuario u
            LEFT JOIN FETCH u.roles r
            LEFT JOIN FETCH r.permisos
            LEFT JOIN FETCH u.empresa
            WHERE u.googleId = :googleId
            """)
    Optional<Usuario> findByGoogleIdWithRolesAndPermisos(@Param("googleId") String googleId);

    @Query("""
            SELECT DISTINCT u FROM Usuario u
            LEFT JOIN FETCH u.roles r
            LEFT JOIN FETCH r.permisos
            LEFT JOIN FETCH u.empresa
            WHERE u.correo = :correo
            """)
    Optional<Usuario> findByCorreoWithRolesAndPermisos(@Param("correo") String correo);
}
