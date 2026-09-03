package com.zam.vendy.controllers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zam.vendy.dtos.auth.AuthResponse;
import com.zam.vendy.dtos.auth.LoginRequest;
import com.zam.vendy.dtos.auth.UserInfoResponse;
import com.zam.vendy.security.CookieUtils;
import com.zam.vendy.security.userdetails.UserDetailsImpl;
import com.zam.vendy.services.AuthService;
import com.zam.vendy.services.AuthService.AuthResult;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final CookieUtils cookieUtils;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResult result = authService.login(request.getNombreUsuario(), request.getContrasena());

        ResponseCookie cookie = cookieUtils.createJwtCookie(result.token());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new AuthResponse("Inicio de sesión exitoso", result.usuario()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        ResponseCookie cookie = cookieUtils.clearJwtCookie();
        SecurityContextHolder.clearContext();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserInfoResponse> me(@AuthenticationPrincipal UserDetailsImpl principal) {
        return ResponseEntity.ok(authService.getCurrentUser(principal));
    }
}
