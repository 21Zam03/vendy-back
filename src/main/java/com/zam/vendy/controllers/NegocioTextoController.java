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

import com.zam.vendy.dtos.negociotexto.NegocioTextoRequest;
import com.zam.vendy.security.userdetails.UserDetailsImpl;
import com.zam.vendy.services.NegocioTextoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/textos")
@RequiredArgsConstructor
public class NegocioTextoController {

    private final NegocioTextoService negocioTextoService;

    @GetMapping
    public ResponseEntity<Map<String, String>> listar(@AuthenticationPrincipal UserDetailsImpl principal) {
        return ResponseEntity.ok(negocioTextoService.listarPropios(principal.getIdUsuario()));
    }

    @PutMapping("/{slot}")
    public ResponseEntity<Void> guardar(@AuthenticationPrincipal UserDetailsImpl principal,
            @PathVariable String slot, @Valid @RequestBody NegocioTextoRequest request) {
        negocioTextoService.guardar(principal.getIdUsuario(), slot, request.getTexto());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{slot}")
    public ResponseEntity<Void> eliminar(@AuthenticationPrincipal UserDetailsImpl principal, @PathVariable String slot) {
        negocioTextoService.eliminar(principal.getIdUsuario(), slot);
        return ResponseEntity.noContent().build();
    }
}
