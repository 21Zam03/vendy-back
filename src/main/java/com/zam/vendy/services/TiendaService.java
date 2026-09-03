package com.zam.vendy.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zam.vendy.entities.Catalogo;
import com.zam.vendy.entities.Categoria;
import com.zam.vendy.entities.ConsultaWhatsapp;
import com.zam.vendy.entities.EnlaceNegocio;
import com.zam.vendy.entities.Negocio;
import com.zam.vendy.entities.Producto;
import com.zam.vendy.entities.Seccion;
import com.zam.vendy.entities.VisitaCatalogo;
import com.zam.vendy.exceptions.ResourceNotFoundException;
import com.zam.vendy.repositories.CatalogoRepository;
import com.zam.vendy.repositories.CategoriaRepository;
import com.zam.vendy.repositories.ConsultaWhatsappRepository;
import com.zam.vendy.repositories.EnlaceNegocioRepository;
import com.zam.vendy.repositories.NegocioRepository;
import com.zam.vendy.repositories.ProductoRepository;
import com.zam.vendy.repositories.SeccionRepository;
import com.zam.vendy.repositories.VisitaCatalogoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TiendaService {

    private final NegocioRepository negocioRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;
    private final VisitaCatalogoRepository visitaCatalogoRepository;
    private final ConsultaWhatsappRepository consultaWhatsappRepository;
    private final EnlaceNegocioRepository enlaceNegocioRepository;
    private final CatalogoRepository catalogoRepository;
    private final SeccionRepository seccionRepository;

    @Transactional(readOnly = true)
    public Negocio obtenerPorSlug(String slug) {
        return negocioRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Tienda no encontrada: " + slug));
    }

    @Transactional(readOnly = true)
    public List<EnlaceNegocio> obtenerEnlaces(Long negocioId) {
        return enlaceNegocioRepository.findByNegocio_IdOrderByOrdenAscIdAsc(negocioId);
    }

    @Transactional(readOnly = true)
    public List<Producto> obtenerDestacados(String slug) {
        Negocio negocio = obtenerPorSlug(slug);
        return productoRepository.findByNegocio_IdAndActivoTrueAndDestacadoTrueOrderByCreatedAtDesc(negocio.getId());
    }

    @Transactional(readOnly = true)
    public List<Seccion> obtenerSecciones(String slug) {
        Negocio negocio = obtenerPorSlug(slug);
        return seccionRepository.findByNegocio_IdOrderByOrdenAscIdAsc(negocio.getId());
    }

    @Transactional(readOnly = true)
    public List<Catalogo> obtenerColecciones(String slug) {
        Negocio negocio = obtenerPorSlug(slug);
        return catalogoRepository.findByNegocio_IdAndActivoTrueOrderByCreatedAtDesc(negocio.getId());
    }

    @Transactional
    public Catalogo obtenerColeccion(String slug, String coleccionSlug) {
        Negocio negocio = obtenerPorSlug(slug);
        Catalogo catalogo = catalogoRepository.findByNegocio_IdAndSlugAndActivoTrue(negocio.getId(), coleccionSlug)
                .orElseThrow(() -> new ResourceNotFoundException("Colección no encontrada"));

        registrarVisita(negocio);
        return catalogo;
    }

    @Transactional
    public CatalogoData obtenerCatalogo(String slug) {
        Negocio negocio = obtenerPorSlug(slug);
        registrarVisita(negocio);

        List<Categoria> categorias = categoriaRepository.findByNegocio_IdOrderByNombreAsc(negocio.getId());
        List<Producto> productos = productoRepository.findByNegocio_IdAndActivoTrue(negocio.getId());

        return new CatalogoData(categorias, productos);
    }

    @Transactional
    public Producto obtenerProducto(String slug, Long productoId) {
        Negocio negocio = obtenerPorSlug(slug);
        Producto producto = productoRepository.findByIdAndNegocio_IdAndActivoTrue(productoId, negocio.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        productoRepository.incrementarVistas(producto.getId());

        return producto;
    }

    @Transactional(readOnly = true)
    public Producto obtenerProductoParaPreview(String slug, Long productoId) {
        Negocio negocio = obtenerPorSlug(slug);
        return productoRepository.findByIdAndNegocio_IdAndActivoTrue(productoId, negocio.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
    }

    @Transactional
    public void registrarConsulta(String slug, Long productoId) {
        Negocio negocio = obtenerPorSlug(slug);
        Producto producto = productoRepository.findByIdAndNegocio_IdAndActivoTrue(productoId, negocio.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        productoRepository.incrementarConsultas(producto.getId());

        ConsultaWhatsapp consulta = ConsultaWhatsapp.builder()
                .negocio(negocio)
                .producto(producto)
                .build();

        consultaWhatsappRepository.save(consulta);
    }

    private void registrarVisita(Negocio negocio) {
        LocalDate hoy = LocalDate.now();
        int actualizadas = visitaCatalogoRepository.incrementarCantidad(negocio.getId(), hoy);

        if (actualizadas == 0) {
            try {
                visitaCatalogoRepository.save(VisitaCatalogo.builder()
                        .negocio(negocio)
                        .fecha(hoy)
                        .cantidad(1)
                        .build());
            } catch (DataIntegrityViolationException exception) {
                visitaCatalogoRepository.incrementarCantidad(negocio.getId(), hoy);
            }
        }
    }

    public record CatalogoData(List<Categoria> categorias, List<Producto> productos) {
    }
}
