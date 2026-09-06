package com.zam.vendy.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.zam.vendy.dtos.negocio.NegocioResponse;
import com.zam.vendy.dtos.negocio.NegocioUpdateRequest;
import com.zam.vendy.dtos.producto.ImagenSubidaResponse;
import com.zam.vendy.entities.Negocio;
import com.zam.vendy.security.userdetails.UserDetailsImpl;
import com.zam.vendy.services.ImagenStorageService;
import com.zam.vendy.services.NegocioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/negocio")
@RequiredArgsConstructor
public class NegocioController {

    private final NegocioService negocioService;
    private final ImagenStorageService imagenStorageService;

    @GetMapping
    public ResponseEntity<NegocioResponse> obtener(@AuthenticationPrincipal UserDetailsImpl principal) {
        Negocio negocio = negocioService.obtenerPorUsuario(principal.getIdUsuario());
        return ResponseEntity.ok(NegocioResponse.from(negocio));
    }

    @PutMapping
    public ResponseEntity<NegocioResponse> guardar(@AuthenticationPrincipal UserDetailsImpl principal,
            @Valid @RequestBody NegocioUpdateRequest request) {
        Negocio negocio = negocioService.guardar(principal.getIdUsuario(), request);
        return ResponseEntity.ok(NegocioResponse.from(negocio));
    }

    // Sube la foto tal cual (sin comprimir) a Firebase Storage y devuelve la URL para
    // incluirla en el siguiente PUT de guardado del negocio.
    @PostMapping("/logo")
    public ResponseEntity<ImagenSubidaResponse> subirLogo(@RequestParam("file") MultipartFile file) {
        String url = imagenStorageService.subirLogoNegocio(file);
        return ResponseEntity.ok(new ImagenSubidaResponse(url));
    }

    @PostMapping("/portada")
    public ResponseEntity<ImagenSubidaResponse> subirPortada(@RequestParam("file") MultipartFile file) {
        String url = imagenStorageService.subirPortadaNegocio(file);
        return ResponseEntity.ok(new ImagenSubidaResponse(url));
    }

    @PostMapping("/fondo")
    public ResponseEntity<ImagenSubidaResponse> subirFondo(@RequestParam("file") MultipartFile file) {
        String url = imagenStorageService.subirFondoNegocio(file);
        return ResponseEntity.ok(new ImagenSubidaResponse(url));
    }
}
