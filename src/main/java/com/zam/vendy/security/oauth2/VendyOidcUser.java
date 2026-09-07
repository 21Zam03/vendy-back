package com.zam.vendy.security.oauth2;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import com.zam.vendy.entities.Usuario;
import com.zam.vendy.security.userdetails.UserDetailsImpl;

// Envuelve el OidcUser que arma Spring con el token de Google, pero con las authorities
// y el nombre REALES del Usuario ya resuelto en nuestra base (ver GoogleAuthService) — así
// el resto de Spring Security (y el JWT que emitimos al terminar el login) trabaja con
// nuestro propio nombreUsuario/roles, no con los claims de Google.
public class VendyOidcUser implements OidcUser {

    private final Usuario usuario;
    private final OidcUser delegate;

    public VendyOidcUser(Usuario usuario, OidcUser delegate) {
        this.usuario = usuario;
        this.delegate = delegate;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    @Override
    public Map<String, Object> getClaims() {
        return delegate.getClaims();
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return delegate.getUserInfo();
    }

    @Override
    public OidcIdToken getIdToken() {
        return delegate.getIdToken();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return delegate.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return UserDetailsImpl.build(usuario).getAuthorities();
    }

    @Override
    public String getName() {
        return usuario.getNombreUsuario();
    }
}
