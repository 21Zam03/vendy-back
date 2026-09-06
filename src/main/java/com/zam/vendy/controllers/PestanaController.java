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

import com.zam.vendy.dtos.pestana.PestanaRequest;
import com.zam.vendy.dtos.pestana.PestanaResponse;
import com.zam.vendy.security.userdetails.UserDetailsImpl;
import com.zam.vendy.services.PestanaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/pestanas")
@RequiredArgsConstructor
public class PestanaController {

    private final PestanaService pestanaService;

    @GetMapping
    public ResponseEntity<List<PestanaResponse>> listar(@AuthenticationPrincipal UserDetailsImpl principal) {
        List<PestanaResponse> pestanas = pestanaService.listar(principal.getIdUsuario()).stream()
                .map(PestanaResponse::from)
                .toList();
        return ResponseEntity.ok(pestanas);
    }

    @PostMapping
    public ResponseEntity<PestanaResponse> crear(@AuthenticationPrincipal UserDetailsImpl principal,
            @Valid @RequestBody PestanaRequest request) {
        PestanaResponse pestana = PestanaResponse.from(pestanaService.crear(principal.getIdUsuario(), request));
        return ResponseEntity.status(HttpStatus.CREATED).body(pestana);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PestanaResponse> actualizar(@AuthenticationPrincipal UserDetailsImpl principal,
            @PathVariable Long id, @Valid @RequestBody PestanaRequest request) {
        PestanaResponse pestana = PestanaResponse.from(
                pestanaService.actualizar(principal.getIdUsuario(), id, request));
        return ResponseEntity.ok(pestana);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@AuthenticationPrincipal UserDetailsImpl principal, @PathVariable Long id) {
        pestanaService.eliminar(principal.getIdUsuario(), id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/orden")
    public ResponseEntity<List<PestanaResponse>> reordenar(@AuthenticationPrincipal UserDetailsImpl principal,
            @RequestBody List<Long> idsEnOrden) {
        List<PestanaResponse> pestanas = pestanaService.reordenar(principal.getIdUsuario(), idsEnOrden).stream()
                .map(PestanaResponse::from)
                .toList();
        return ResponseEntity.ok(pestanas);
    }
}
