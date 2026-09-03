package com.zam.vendy.security.userdetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.zam.vendy.entities.Permiso;
import com.zam.vendy.entities.Rol;
import com.zam.vendy.entities.Usuario;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserDetailsImpl implements UserDetails {

    private final Usuario usuario;

    public static UserDetailsImpl build(Usuario usuario) {
        return new UserDetailsImpl(usuario);
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Integer getIdUsuario() {
        return usuario.getIdUsuario();
    }

    public Long getIdEmpresa() {
        return usuario.getEmpresa() != null ? usuario.getEmpresa().getIdEmpresa() : null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();

        for (Rol rol : usuario.getRoles()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + rol.getNombre().toUpperCase()));
            for (Permiso permiso : rol.getPermisos()) {
                authorities.add(new SimpleGrantedAuthority(permiso.getNombre()));
            }
        }

        return authorities;
    }

    @Override
    public String getPassword() {
        return usuario.getContrasena();
    }

    @Override
    public String getUsername() {
        return usuario.getNombreUsuario();
    }

    @Override
    public boolean isAccountNonExpired() {
        return !Boolean.TRUE.equals(usuario.getCuentaExpirada());
    }

    @Override
    public boolean isAccountNonLocked() {
        return !Boolean.TRUE.equals(usuario.getCuentaBloqueada());
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !Boolean.TRUE.equals(usuario.getCredencialesExpiradas());
    }

    @Override
    public boolean isEnabled() {
        return Boolean.TRUE.equals(usuario.getActivo());
    }
}
