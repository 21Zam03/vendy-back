package com.zam.vendy.controllers;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zam.vendy.dtos.negociobanner.NegocioBannerRequest;
import com.zam.vendy.security.userdetails.UserDetailsImpl;
import com.zam.vendy.services.NegocioBannerService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/banners")
@RequiredArgsConstructor
public class NegocioBannerController {

    private final NegocioBannerService negocioBannerService;

    @GetMapping
    public ResponseEntity<Map<String, String>> listar(@AuthenticationPrincipal UserDetailsImpl principal) {
        return ResponseEntity.ok(negocioBannerService.listarPropios(principal.getIdUsuario()));
    }

    @PutMapping("/{slot}")
    public ResponseEntity<Void> guardar(@AuthenticationPrincipal UserDetailsImpl principal,
            @PathVariable String slot, @Valid @RequestBody NegocioBannerRequest request) {
        negocioBannerService.guardar(principal.getIdUsuario(), slot, request.getImagenUrl());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{slot}")
    public ResponseEntity<Void> eliminar(@AuthenticationPrincipal UserDetailsImpl principal, @PathVariable String slot) {
        negocioBannerService.eliminar(principal.getIdUsuario(), slot);
        return ResponseEntity.noContent().build();
    }
}
