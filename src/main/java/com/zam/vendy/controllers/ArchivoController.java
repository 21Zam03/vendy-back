package com.zam.vendy.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.zam.vendy.dtos.archivo.ArchivoResponse;
import com.zam.vendy.security.userdetails.UserDetailsImpl;
import com.zam.vendy.services.ArchivoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/archivos")
@RequiredArgsConstructor
public class ArchivoController {

    private final ArchivoService archivoService;

    @GetMapping
    public ResponseEntity<List<ArchivoResponse>> listar(@AuthenticationPrincipal UserDetailsImpl principal) {
        List<ArchivoResponse> archivos = archivoService.listar(principal.getIdUsuario()).stream()
                .map(ArchivoResponse::from)
                .toList();
        return ResponseEntity.ok(archivos);
    }

    @PostMapping
    public ResponseEntity<ArchivoResponse> subir(@AuthenticationPrincipal UserDetailsImpl principal,
            @RequestParam("file") MultipartFile file) {
        ArchivoResponse archivo = ArchivoResponse.from(archivoService.subir(principal.getIdUsuario(), file));
        return ResponseEntity.status(HttpStatus.CREATED).body(archivo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@AuthenticationPrincipal UserDetailsImpl principal, @PathVariable Long id) {
        archivoService.eliminar(principal.getIdUsuario(), id);
        return ResponseEntity.noContent().build();
    }
}
