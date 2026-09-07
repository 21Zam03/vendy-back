package com.zam.vendy.security.oauth2;

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import com.zam.vendy.entities.Usuario;
import com.zam.vendy.services.GoogleAuthService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomOidcUserService extends OidcUserService {

    private final GoogleAuthService googleAuthService;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);

        String correo = oidcUser.getEmail();
        if (correo == null || !Boolean.TRUE.equals(oidcUser.getEmailVerified())) {
            // Sin esto, alguien podría entrar con un correo de Google sin verificar y
            // vincularse/crear una cuenta con un correo que no le pertenece de verdad.
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("correo_no_verificado"),
                    "Tu cuenta de Google no tiene un correo verificado");
        }

        Usuario usuario = googleAuthService.procesarLoginGoogle(
                oidcUser.getSubject(), correo, oidcUser.getGivenName(), oidcUser.getFamilyName());

        return new VendyOidcUser(usuario, oidcUser);
    }
}
