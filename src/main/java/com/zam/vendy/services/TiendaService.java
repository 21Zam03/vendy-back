package com.zam.vendy.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import com.zam.vendy.entities.VisitaEvento;
import com.zam.vendy.exceptions.ResourceNotFoundException;
import com.zam.vendy.repositories.CatalogoRepository;
import com.zam.vendy.repositories.CategoriaRepository;
import com.zam.vendy.repositories.ConsultaWhatsappRepository;
import com.zam.vendy.repositories.EnlaceNegocioRepository;
import com.zam.vendy.repositories.NegocioRepository;
import com.zam.vendy.repositories.ProductoRepository;
import com.zam.vendy.repositories.SeccionRepository;
import com.zam.vendy.repositories.VisitaCatalogoRepository;
import com.zam.vendy.repositories.VisitaEventoRepository;
import com.zam.vendy.security.userdetails.UserDetailsImpl;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TiendaService {

    private final NegocioRepository negocioRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;
    private final VisitaCatalogoRepository visitaCatalogoRepository;
    private final VisitaEventoRepository visitaEventoRepository;
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
    public Catalogo obtenerColeccion(String slug, String coleccionSlug, String visitorId) {
        Negocio negocio = obtenerPorSlug(slug);
        Catalogo catalogo = catalogoRepository.findByNegocio_IdAndSlugAndActivoTrue(negocio.getId(), coleccionSlug)
                .orElseThrow(() -> new ResourceNotFoundException("Colección no encontrada"));

        registrarVisita(negocio, visitorId);
        return catalogo;
    }

    @Transactional
    public CatalogoData obtenerCatalogo(String slug, String visitorId) {
        Negocio negocio = obtenerPorSlug(slug);
        registrarVisita(negocio, visitorId);

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

    // Solo cuenta visitantes reales: se excluye al dueño del negocio (si está logueado
    // en el mismo navegador, ej. probando su propia tienda) y se deduplica por
    // visitorId+día, así que refrescar la página varias veces no infla el número.
    private void registrarVisita(Negocio negocio, String visitorId) {
        if (visitorId == null || visitorId.isBlank() || esPropietario(negocio)) {
            return;
        }

        LocalDate hoy = LocalDate.now();

        try {
            visitaEventoRepository.save(VisitaEvento.builder()
                    .negocio(negocio)
                    .visitorId(visitorId)
                    .fecha(hoy)
                    .build());
        } catch (DataIntegrityViolationException exception) {
            return; // ya se contó a este visitante hoy
        }

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

    private boolean esPropietario(Negocio negocio) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserDetailsImpl principal)) {
            return false;
        }
        return negocioRepository.existsByIdAndUsuario_IdUsuario(negocio.getId(), principal.getIdUsuario());
    }

    public record CatalogoData(List<Categoria> categorias, List<Producto> productos) {
    }
}
