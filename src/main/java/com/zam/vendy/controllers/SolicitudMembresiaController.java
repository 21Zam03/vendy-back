package com.zam.vendy.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zam.vendy.dtos.solicitudmembresia.SolicitudMembresiaRequest;
import com.zam.vendy.security.userdetails.UserDetailsImpl;
import com.zam.vendy.services.SolicitudMembresiaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

// A diferencia de solicitudes-registro, este SÍ requiere login: hace falta saber a qué
// negocio pertenece el pedido de cambio de plan.
@RestController
@RequestMapping("/api/v1/solicitudes-membresia")
@RequiredArgsConstructor
public class SolicitudMembresiaController {

    private final SolicitudMembresiaService solicitudMembresiaService;

    @PostMapping
    public ResponseEntity<Void> crear(@AuthenticationPrincipal UserDetailsImpl principal,
            @Valid @RequestBody SolicitudMembresiaRequest request) {
        solicitudMembresiaService.crear(principal.getIdUsuario(), request.getPlan());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
