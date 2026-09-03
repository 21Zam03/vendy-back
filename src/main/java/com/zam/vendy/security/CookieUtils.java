package com.zam.vendy.security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class CookieUtils {

    @Value("${parameters.cookie_name}")
    private String cookieName;

    @Value("${parameters.cookie_max_age}")
    private long cookieMaxAge;

    @Value("${parameters.cookie_is_secure}")
    private boolean cookieSecure;

    @Value("${parameters.cookie_http_only}")
    private boolean cookieHttpOnly;

    @Value("${parameters.cookie_path}")
    private String cookiePath;

    public String getCookieName() {
        return cookieName;
    }

    public ResponseCookie createJwtCookie(String token) {
        return ResponseCookie.from(cookieName, token)
                .httpOnly(cookieHttpOnly)
                .secure(cookieSecure)
                .path(cookiePath)
                .maxAge(cookieMaxAge)
                .sameSite(resolveSameSite())
                .build();
    }

    public ResponseCookie clearJwtCookie() {
        return ResponseCookie.from(cookieName, "")
                .httpOnly(cookieHttpOnly)
                .secure(cookieSecure)
                .path(cookiePath)
                .maxAge(0)
                .sameSite(resolveSameSite())
                .build();
    }

    // Frontend y backend viven en dominios distintos (vendy-front.vercel.app vs
    // api2.zavefy.com) => la cookie es "cross-site" de verdad. SameSite=Lax nunca se
    // manda en llamadas fetch/XHR entre sitios distintos, solo en navegaciones de página
    // completa — por eso el login "funcionaba" pero todo lo demás daba 401.
    // SameSite=None es obligatorio para que el navegador la reenvíe en esos fetch, pero
    // los navegadores exigen que además sea Secure (solo funciona con HTTPS real), así
    // que en dev (http, sin secure) hay que seguir usando Lax.
    private String resolveSameSite() {
        return cookieSecure ? "None" : "Lax";
    }

    public String extractTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }

        return Arrays.stream(cookies)
                .filter(cookie -> cookieName.equals(cookie.getName()))
                .map(Cookie::getValue)
                .filter(StringUtils::hasText)
                .findFirst()
                .orElse(null);
    }
}
