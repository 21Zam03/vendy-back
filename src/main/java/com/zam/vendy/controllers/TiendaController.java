package com.zam.vendy.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zam.vendy.dtos.categoria.CategoriaResponse;
import com.zam.vendy.dtos.pestana.PestanaResponse;
import com.zam.vendy.dtos.seccion.SeccionResponse;
import com.zam.vendy.dtos.tienda.CatalogoResponse;
import com.zam.vendy.dtos.tienda.ColeccionPublicaResponse;
import com.zam.vendy.dtos.tienda.ColeccionResumenResponse;
import com.zam.vendy.dtos.tienda.NegocioPublicoResponse;
import com.zam.vendy.dtos.tienda.ProductoPublicoResponse;
import com.zam.vendy.entities.EnlaceNegocio;
import com.zam.vendy.entities.Negocio;
import com.zam.vendy.services.TiendaService;
import com.zam.vendy.services.TiendaService.CatalogoData;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/tienda/{slug}")
@RequiredArgsConstructor
public class TiendaController {

    private final TiendaService tiendaService;

    @GetMapping
    public ResponseEntity<NegocioPublicoResponse> perfil(@PathVariable String slug) {
        Negocio negocio = tiendaService.obtenerPorSlug(slug);
        List<EnlaceNegocio> enlaces = tiendaService.obtenerEnlaces(negocio.getId());
        return ResponseEntity.ok(NegocioPublicoResponse.from(negocio, enlaces));
    }

    @GetMapping("/destacados")
    public ResponseEntity<List<ProductoPublicoResponse>> destacados(@PathVariable String slug) {
        List<ProductoPublicoResponse> destacados = tiendaService.obtenerDestacados(slug).stream()
                .map(ProductoPublicoResponse::from)
                .toList();
        return ResponseEntity.ok(destacados);
    }

    @GetMapping("/catalogo")
    public ResponseEntity<CatalogoResponse> catalogo(@PathVariable String slug,
            @RequestHeader(value = "X-Visitor-Id", required = false) String visitorId) {
        CatalogoData datos = tiendaService.obtenerCatalogo(slug, visitorId);

        List<CategoriaResponse> categorias = datos.categorias().stream()
                .map(CategoriaResponse::from)
                .toList();
        List<ProductoPublicoResponse> productos = datos.productos().stream()
                .map(ProductoPublicoResponse::from)
                .toList();

        return ResponseEntity.ok(new CatalogoResponse(categorias, productos));
    }

    @GetMapping("/secciones")
    public ResponseEntity<List<SeccionResponse>> secciones(@PathVariable String slug) {
        List<SeccionResponse> secciones = tiendaService.obtenerSecciones(slug).stream()
                .map(SeccionResponse::from)
                .toList();
        return ResponseEntity.ok(secciones);
    }

    @GetMapping("/pestanas")
    public ResponseEntity<List<PestanaResponse>> pestanas(@PathVariable String slug) {
        List<PestanaResponse> pestanas = tiendaService.obtenerPestanas(slug).stream()
                .map(PestanaResponse::from)
                .toList();
        return ResponseEntity.ok(pestanas);
    }

    @GetMapping("/banners")
    public ResponseEntity<Map<String, String>> banners(@PathVariable String slug) {
        return ResponseEntity.ok(tiendaService.obtenerBanners(slug));
    }

    @GetMapping("/textos")
    public ResponseEntity<Map<String, String>> textos(@PathVariable String slug) {
        return ResponseEntity.ok(tiendaService.obtenerTextos(slug));
    }

    @GetMapping("/colecciones")
    public ResponseEntity<List<ColeccionResumenResponse>> colecciones(@PathVariable String slug) {
        List<ColeccionResumenResponse> colecciones = tiendaService.obtenerColecciones(slug).stream()
                .map(ColeccionResumenResponse::from)
                .toList();
        return ResponseEntity.ok(colecciones);
    }

    @GetMapping("/colecciones/{coleccionSlug}")
    public ResponseEntity<ColeccionPublicaResponse> coleccion(@PathVariable String slug,
            @PathVariable String coleccionSlug,
            @RequestHeader(value = "X-Visitor-Id", required = false) String visitorId) {
        return ResponseEntity.ok(ColeccionPublicaResponse.from(
                tiendaService.obtenerColeccion(slug, coleccionSlug, visitorId)));
    }

    @GetMapping("/productos/{productoId}")
    public ResponseEntity<ProductoPublicoResponse> producto(@PathVariable String slug,
            @PathVariable Long productoId) {
        return ResponseEntity.ok(ProductoPublicoResponse.from(tiendaService.obtenerProducto(slug, productoId)));
    }

    @PostMapping("/productos/{productoId}/consulta")
    public ResponseEntity<Void> consulta(@PathVariable String slug, @PathVariable Long productoId) {
        tiendaService.registrarConsulta(slug, productoId);
        return ResponseEntity.noContent().build();
    }
}
