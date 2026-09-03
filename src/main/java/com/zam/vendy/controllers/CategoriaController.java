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

import com.zam.vendy.dtos.categoria.CategoriaRequest;
import com.zam.vendy.dtos.categoria.CategoriaResponse;
import com.zam.vendy.security.userdetails.UserDetailsImpl;
import com.zam.vendy.services.CategoriaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;

    @GetMapping
    public ResponseEntity<List<CategoriaResponse>> listar(@AuthenticationPrincipal UserDetailsImpl principal) {
        List<CategoriaResponse> categorias = categoriaService.listar(principal.getIdUsuario()).stream()
                .map(CategoriaResponse::from)
                .toList();
        return ResponseEntity.ok(categorias);
    }

    @PostMapping
    public ResponseEntity<CategoriaResponse> crear(@AuthenticationPrincipal UserDetailsImpl principal,
            @Valid @RequestBody CategoriaRequest request) {
        CategoriaResponse categoria = CategoriaResponse.from(
                categoriaService.crear(principal.getIdUsuario(), request));
        return ResponseEntity.status(HttpStatus.CREATED).body(categoria);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponse> actualizar(@AuthenticationPrincipal UserDetailsImpl principal,
            @PathVariable Long id, @Valid @RequestBody CategoriaRequest request) {
        CategoriaResponse categoria = CategoriaResponse.from(
                categoriaService.actualizar(principal.getIdUsuario(), id, request));
        return ResponseEntity.ok(categoria);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@AuthenticationPrincipal UserDetailsImpl principal, @PathVariable Long id) {
        categoriaService.eliminar(principal.getIdUsuario(), id);
        return ResponseEntity.noContent().build();
    }
}
