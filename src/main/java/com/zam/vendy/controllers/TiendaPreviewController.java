package com.zam.vendy.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zam.vendy.entities.Negocio;
import com.zam.vendy.entities.Producto;
import com.zam.vendy.services.TiendaService;
import com.zam.vendy.util.OgPreviewHtml;

import lombok.RequiredArgsConstructor;

/**
 * Páginas de preview (Open Graph / Twitter Card) para compartir por WhatsApp y redes.
 * A diferencia de TiendaController, estos endpoints devuelven HTML ya renderizado
 * porque los bots que generan el preview de los links no ejecutan JavaScript.
 */
@RestController
@RequestMapping("/api/v1/tienda/{slug}")
@RequiredArgsConstructor
public class TiendaPreviewController {

    private static final String SITE_NAME = "Vendy";

    private final TiendaService tiendaService;

    @Value("${app.public-frontend-url}")
    private String frontendUrl;

    @GetMapping(value = "/preview", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> perfilPreview(@PathVariable String slug) {
        Negocio negocio = tiendaService.obtenerPorSlug(slug);
        String targetUrl = frontendUrl + "/tienda/" + slug;

        String html = OgPreviewHtml.build(negocio.getNombre(), negocio.getDescripcion(), targetUrl, SITE_NAME);
        return ResponseEntity.ok(html);
    }

    @GetMapping(value = "/producto/{productoId}/preview", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> productoPreview(@PathVariable String slug, @PathVariable Long productoId) {
        Producto producto = tiendaService.obtenerProductoParaPreview(slug, productoId);
        String targetUrl = frontendUrl + "/tienda/" + slug + "/producto/" + productoId;
        String title = producto.getNombre() + " · " + producto.getNegocio().getNombre();

        String html = OgPreviewHtml.build(title, producto.getDescripcion(), targetUrl, SITE_NAME);
        return ResponseEntity.ok(html);
    }
}
