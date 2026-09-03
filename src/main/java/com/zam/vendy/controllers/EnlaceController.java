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

import com.zam.vendy.dtos.enlace.EnlaceRequest;
import com.zam.vendy.dtos.enlace.EnlaceResponse;
import com.zam.vendy.security.userdetails.UserDetailsImpl;
import com.zam.vendy.services.EnlaceService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/enlaces")
@RequiredArgsConstructor
public class EnlaceController {

    private final EnlaceService enlaceService;

    @GetMapping
    public ResponseEntity<List<EnlaceResponse>> listar(@AuthenticationPrincipal UserDetailsImpl principal) {
        List<EnlaceResponse> enlaces = enlaceService.listar(principal.getIdUsuario()).stream()
                .map(EnlaceResponse::from)
                .toList();
        return ResponseEntity.ok(enlaces);
    }

    @PostMapping
    public ResponseEntity<EnlaceResponse> crear(@AuthenticationPrincipal UserDetailsImpl principal,
            @Valid @RequestBody EnlaceRequest request) {
        EnlaceResponse enlace = EnlaceResponse.from(enlaceService.crear(principal.getIdUsuario(), request));
        return ResponseEntity.status(HttpStatus.CREATED).body(enlace);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EnlaceResponse> actualizar(@AuthenticationPrincipal UserDetailsImpl principal,
            @PathVariable Long id, @Valid @RequestBody EnlaceRequest request) {
        EnlaceResponse enlace = EnlaceResponse.from(enlaceService.actualizar(principal.getIdUsuario(), id, request));
        return ResponseEntity.ok(enlace);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@AuthenticationPrincipal UserDetailsImpl principal, @PathVariable Long id) {
        enlaceService.eliminar(principal.getIdUsuario(), id);
        return ResponseEntity.noContent().build();
    }
}
