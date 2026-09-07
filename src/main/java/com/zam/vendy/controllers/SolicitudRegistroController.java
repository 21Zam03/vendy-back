package com.zam.vendy.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zam.vendy.dtos.solicitudregistro.SolicitudRegistroRequest;
import com.zam.vendy.services.SolicitudRegistroService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

// Endpoint público (sin login): el formulario de registro todavía no crea la cuenta sola,
// solo guarda el pedido para que el equipo se comunique por teléfono y la arme a mano.
@RestController
@RequestMapping("/api/v1/solicitudes-registro")
@RequiredArgsConstructor
public class SolicitudRegistroController {

    private final SolicitudRegistroService solicitudRegistroService;

    @PostMapping
    public ResponseEntity<Void> crear(@Valid @RequestBody SolicitudRegistroRequest request) {
        solicitudRegistroService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
