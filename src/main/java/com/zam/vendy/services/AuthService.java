package com.zam.vendy.services;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.zam.vendy.dtos.auth.UserInfoResponse;
import com.zam.vendy.entities.Rol;
import com.zam.vendy.entities.Usuario;
import com.zam.vendy.security.jwt.JwtUtils;
import com.zam.vendy.security.userdetails.UserDetailsImpl;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public AuthResult login(String nombreUsuario, String contrasena) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(nombreUsuario, contrasena));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtUtils.createToken(authentication);
        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();

        return new AuthResult(token, buildUserInfo(principal));
    }

    public UserInfoResponse getCurrentUser(UserDetailsImpl principal) {
        return buildUserInfo(principal);
    }

    private UserInfoResponse buildUserInfo(UserDetailsImpl principal) {
        Usuario usuario = principal.getUsuario();

        Set<String> roles = usuario.getRoles().stream()
                .map(Rol::getNombre)
                .collect(Collectors.toSet());

        return UserInfoResponse.builder()
                .idUsuario(usuario.getIdUsuario())
                .nombreUsuario(usuario.getNombreUsuario())
                .nombres(usuario.getNombres())
                .apellidos(usuario.getApellidos())
                .correo(usuario.getCorreo())
                .idEmpresa(principal.getIdEmpresa())
                .roles(roles)
                .build();
    }

    public record AuthResult(String token, UserInfoResponse usuario) {
    }
}
