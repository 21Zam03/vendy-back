package com.zam.vendy.security.oauth2;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.zam.vendy.entities.Usuario;
import com.zam.vendy.security.CookieUtils;
import com.zam.vendy.security.jwt.JwtUtils;
import com.zam.vendy.security.userdetails.UserDetailsImpl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

// Termina el login con Google exactamente como termina el login normal (AuthController):
// arma la misma cookie JWT httpOnly, para que el resto del backend (JwtAuthenticationFilter,
// todos los controllers) no tenga que enterarse de que este login vino de Google.
// Redirige siempre a "Mi negocio" — una cuenta nueva por Google no tiene Negocio todavía
// (a diferencia del alta manual, acá no se pidió teléfono/nombre de negocio antes), y esa
// pantalla ya sabe mostrar "Todavía no has creado tu negocio" y dejar completarlo ahí mismo.
@Component
@RequiredArgsConstructor
public class GoogleAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private static final String DESTINO = "/mi-negocio";

    private final JwtUtils jwtUtils;
    private final CookieUtils cookieUtils;

    @Value("${app.public-frontend-url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException {
        VendyOidcUser principal = (VendyOidcUser) authentication.getPrincipal();
        Usuario usuario = principal.getUsuario();

        UsernamePasswordAuthenticationToken jwtAuthentication = new UsernamePasswordAuthenticationToken(
                UserDetailsImpl.build(usuario), null, principal.getAuthorities());

        String token = jwtUtils.createToken(jwtAuthentication);
        ResponseCookie cookie = cookieUtils.createJwtCookie(token);

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        response.sendRedirect(frontendUrl + DESTINO);
    }
}
