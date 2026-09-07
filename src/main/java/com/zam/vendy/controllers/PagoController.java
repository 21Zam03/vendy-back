package com.zam.vendy.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zam.vendy.dtos.pago.PagoResponse;
import com.zam.vendy.security.userdetails.UserDetailsImpl;
import com.zam.vendy.services.PagoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/pagos")
@RequiredArgsConstructor
public class PagoController {

    private final PagoService pagoService;

    @GetMapping
    public ResponseEntity<List<PagoResponse>> listar(@AuthenticationPrincipal UserDetailsImpl principal) {
        List<PagoResponse> pagos = pagoService.listar(principal.getIdUsuario()).stream()
                .map(PagoResponse::from)
                .toList();
        return ResponseEntity.ok(pagos);
    }
}
