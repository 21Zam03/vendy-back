package com.zam.vendy.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zam.vendy.dtos.catalogo.CatalogoRequest;
import com.zam.vendy.entities.Catalogo;
import com.zam.vendy.entities.Negocio;
import com.zam.vendy.entities.Producto;
import com.zam.vendy.entities.embeddable.Apariencia;
import com.zam.vendy.entities.enums.CatalogLayout;
import com.zam.vendy.exceptions.ResourceNotFoundException;
import com.zam.vendy.exceptions.SlugYaExisteException;
import com.zam.vendy.repositories.CatalogoRepository;
import com.zam.vendy.repositories.ProductoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CatalogoService {

    private final CatalogoRepository catalogoRepository;
    private final ProductoRepository productoRepository;
    private final NegocioService negocioService;

    @Transactional(readOnly = true)
    public List<Catalogo> listar(Integer idUsuario) {
        Negocio negocio = negocioService.obtenerPorUsuario(idUsuario);
        return catalogoRepository.findByNegocio_IdOrderByCreatedAtDesc(negocio.getId());
    }

    @Transactional
    public Catalogo crear(Integer idUsuario, CatalogoRequest request) {
        Negocio negocio = negocioService.obtenerPorUsuario(idUsuario);
        validarSlugDisponible(negocio.getId(), request.getSlug(), null);

        Catalogo catalogo = Catalogo.builder()
                .negocio(negocio)
                .nombre(request.getNombre())
                .slug(request.getSlug())
                .activo(request.getActivo())
                .apariencia(construirApariencia(request))
                .productos(resolverProductos(negocio.getId(), request.getProductoIds()))
                .build();

        return catalogoRepository.save(catalogo);
    }

    @Transactional
    public Catalogo actualizar(Integer idUsuario, Long catalogoId, CatalogoRequest request) {
        Catalogo catalogo = obtenerPropio(idUsuario, catalogoId);
        validarSlugDisponible(catalogo.getNegocio().getId(), request.getSlug(), catalogoId);

        catalogo.setNombre(request.getNombre());
        catalogo.setSlug(request.getSlug());
        catalogo.setActivo(request.getActivo());
        catalogo.setApariencia(construirApariencia(request));
        catalogo.setProductos(resolverProductos(catalogo.getNegocio().getId(), request.getProductoIds()));

        return catalogo;
    }

    @Transactional
    public void eliminar(Integer idUsuario, Long catalogoId) {
        Catalogo catalogo = obtenerPropio(idUsuario, catalogoId);
        catalogoRepository.delete(catalogo);
    }

    private Apariencia construirApariencia(CatalogoRequest request) {
        return Apariencia.builder()
                .accentColor(request.getAccentColor())
                .background(request.getBackground())
                .font(request.getFont())
                .radius(request.getRadius())
                .cover(request.getCover())
                .catalogLayout(request.getCatalogLayout() != null ? request.getCatalogLayout() : CatalogLayout.GRID)
                .build();
    }

    private Set<Producto> resolverProductos(Long negocioId, List<Long> productoIds) {
        if (productoIds == null || productoIds.isEmpty()) {
            return new HashSet<>();
        }
        List<Producto> productos = productoRepository.findAllById(productoIds);
        boolean todosSonDelNegocio = productos.stream()
                .allMatch(p -> p.getNegocio() != null && p.getNegocio().getId().equals(negocioId));
        if (!todosSonDelNegocio) {
            throw new ResourceNotFoundException("Uno o más productos no pertenecen a tu negocio");
        }
        return new HashSet<>(productos);
    }

    private void validarSlugDisponible(Long negocioId, String slug, Long catalogoIdActual) {
        catalogoRepository.findByNegocio_IdAndSlug(negocioId, slug)
                .filter(existente -> !existente.getId().equals(catalogoIdActual))
                .ifPresent(existente -> {
                    throw new SlugYaExisteException(slug);
                });
    }

    private Catalogo obtenerPropio(Integer idUsuario, Long catalogoId) {
        Negocio negocio = negocioService.obtenerPorUsuario(idUsuario);
        return catalogoRepository.findByIdAndNegocio_Id(catalogoId, negocio.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Colección no encontrada"));
    }
}
