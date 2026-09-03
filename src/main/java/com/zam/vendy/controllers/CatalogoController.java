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

import com.zam.vendy.dtos.catalogo.CatalogoRequest;
import com.zam.vendy.dtos.catalogo.CatalogoResponse;
import com.zam.vendy.security.userdetails.UserDetailsImpl;
import com.zam.vendy.services.CatalogoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/catalogos")
@RequiredArgsConstructor
public class CatalogoController {

    private final CatalogoService catalogoService;

    @GetMapping
    public ResponseEntity<List<CatalogoResponse>> listar(@AuthenticationPrincipal UserDetailsImpl principal) {
        List<CatalogoResponse> catalogos = catalogoService.listar(principal.getIdUsuario()).stream()
                .map(CatalogoResponse::from)
                .toList();
        return ResponseEntity.ok(catalogos);
    }

    @PostMapping
    public ResponseEntity<CatalogoResponse> crear(@AuthenticationPrincipal UserDetailsImpl principal,
            @Valid @RequestBody CatalogoRequest request) {
        CatalogoResponse catalogo = CatalogoResponse.from(catalogoService.crear(principal.getIdUsuario(), request));
        return ResponseEntity.status(HttpStatus.CREATED).body(catalogo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CatalogoResponse> actualizar(@AuthenticationPrincipal UserDetailsImpl principal,
            @PathVariable Long id, @Valid @RequestBody CatalogoRequest request) {
        CatalogoResponse catalogo = CatalogoResponse.from(
                catalogoService.actualizar(principal.getIdUsuario(), id, request));
        return ResponseEntity.ok(catalogo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@AuthenticationPrincipal UserDetailsImpl principal, @PathVariable Long id) {
        catalogoService.eliminar(principal.getIdUsuario(), id);
        return ResponseEntity.noContent().build();
    }
}
