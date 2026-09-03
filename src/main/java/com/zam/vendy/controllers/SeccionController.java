package com.zam.vendy.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zam.vendy.dtos.seccion.SeccionRequest;
import com.zam.vendy.dtos.seccion.SeccionResponse;
import com.zam.vendy.security.userdetails.UserDetailsImpl;
import com.zam.vendy.services.SeccionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/secciones")
@RequiredArgsConstructor
public class SeccionController {

    private final SeccionService seccionService;

    @GetMapping
    public ResponseEntity<List<SeccionResponse>> listar(@AuthenticationPrincipal UserDetailsImpl principal) {
        List<SeccionResponse> secciones = seccionService.listar(principal.getIdUsuario()).stream()
                .map(SeccionResponse::from)
                .toList();
        return ResponseEntity.ok(secciones);
    }

    @PostMapping
    public ResponseEntity<SeccionResponse> crear(@AuthenticationPrincipal UserDetailsImpl principal,
            @Valid @RequestBody SeccionRequest request) {
        SeccionResponse seccion = SeccionResponse.from(seccionService.crear(principal.getIdUsuario(), request));
        return ResponseEntity.status(HttpStatus.CREATED).body(seccion);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SeccionResponse> actualizar(@AuthenticationPrincipal UserDetailsImpl principal,
            @PathVariable Long id, @Valid @RequestBody SeccionRequest request) {
        SeccionResponse seccion = SeccionResponse.from(
                seccionService.actualizar(principal.getIdUsuario(), id, request));
        return ResponseEntity.ok(seccion);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@AuthenticationPrincipal UserDetailsImpl principal, @PathVariable Long id) {
        seccionService.eliminar(principal.getIdUsuario(), id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/orden")
    public ResponseEntity<List<SeccionResponse>> reordenar(@AuthenticationPrincipal UserDetailsImpl principal,
            @RequestBody List<Long> idsEnOrden) {
        List<SeccionResponse> secciones = seccionService.reordenar(principal.getIdUsuario(), idsEnOrden).stream()
                .map(SeccionResponse::from)
                .toList();
        return ResponseEntity.ok(secciones);
    }
}
