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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.zam.vendy.dtos.producto.ImagenSubidaResponse;
import com.zam.vendy.dtos.producto.ProductoRequest;
import com.zam.vendy.dtos.producto.ProductoResponse;
import com.zam.vendy.security.userdetails.UserDetailsImpl;
import com.zam.vendy.services.ImagenStorageService;
import com.zam.vendy.services.ProductoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;
    private final ImagenStorageService imagenStorageService;

    @GetMapping
    public ResponseEntity<List<ProductoResponse>> listar(@AuthenticationPrincipal UserDetailsImpl principal) {
        List<ProductoResponse> productos = productoService.listar(principal.getIdUsuario()).stream()
                .map(ProductoResponse::from)
                .toList();
        return ResponseEntity.ok(productos);
    }

    @PostMapping
    public ResponseEntity<ProductoResponse> crear(@AuthenticationPrincipal UserDetailsImpl principal,
            @Valid @RequestBody ProductoRequest request) {
        ProductoResponse producto = ProductoResponse.from(
                productoService.crear(principal.getIdUsuario(), request));
        return ResponseEntity.status(HttpStatus.CREATED).body(producto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponse> actualizar(@AuthenticationPrincipal UserDetailsImpl principal,
            @PathVariable Long id, @Valid @RequestBody ProductoRequest request) {
        ProductoResponse producto = ProductoResponse.from(
                productoService.actualizar(principal.getIdUsuario(), id, request));
        return ResponseEntity.ok(producto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@AuthenticationPrincipal UserDetailsImpl principal, @PathVariable Long id) {
        productoService.eliminar(principal.getIdUsuario(), id);
        return ResponseEntity.noContent().build();
    }

    // Sube la imagen tal cual (sin comprimir) a Firebase Storage y devuelve la URL para
    // incluirla en el siguiente POST/PUT de creación o edición del producto.
    @PostMapping("/imagen")
    public ResponseEntity<ImagenSubidaResponse> subirImagen(@RequestParam("file") MultipartFile file) {
        String url = imagenStorageService.subirImagenProducto(file);
        return ResponseEntity.ok(new ImagenSubidaResponse(url));
    }
}
