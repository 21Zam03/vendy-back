package com.zam.vendy.security.oauth2;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// Si Google rechaza el login o CustomOidcUserService lo corta (ej. correo no verificado),
// vuelve al login del frontend en vez de mostrar la pantalla de error genérica de Spring.
@Component
public class GoogleAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private static final Logger log = LoggerFactory.getLogger(GoogleAuthenticationFailureHandler.class);

    @Value("${app.public-frontend-url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException {
        // Sin esto, la causa real (client_secret mal, correo no verificado, token exchange
        // fallido, etc.) se perdía — el usuario solo veía un mensaje genérico y no había
        // forma de saber por qué desde el servidor.
        log.error("Falló el login con Google", exception);
        String mensaje = UriUtils.encode(
                "No se pudo iniciar sesión con Google. Intenta de nuevo o usa tu usuario y contraseña.",
                StandardCharsets.UTF_8);
        response.sendRedirect(frontendUrl + "/login?error=" + mensaje);
    }
}
