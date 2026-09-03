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
                .sameSite("Lax")
                .build();
    }

    public ResponseCookie clearJwtCookie() {
        return ResponseCookie.from(cookieName, "")
                .httpOnly(cookieHttpOnly)
                .secure(cookieSecure)
                .path(cookiePath)
                .maxAge(0)
                .sameSite("Lax")
                .build();
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
