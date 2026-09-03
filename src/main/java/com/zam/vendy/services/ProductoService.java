package com.zam.vendy.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zam.vendy.dtos.producto.ProductoRequest;
import com.zam.vendy.entities.Categoria;
import com.zam.vendy.entities.Negocio;
import com.zam.vendy.entities.Producto;
import com.zam.vendy.entities.Seccion;
import com.zam.vendy.exceptions.ResourceNotFoundException;
import com.zam.vendy.repositories.CategoriaRepository;
import com.zam.vendy.repositories.ProductoRepository;
import com.zam.vendy.repositories.SeccionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final SeccionRepository seccionRepository;
    private final NegocioService negocioService;

    @Transactional(readOnly = true)
    public List<Producto> listar(Integer idUsuario) {
        Negocio negocio = negocioService.obtenerPorUsuario(idUsuario);
        return productoRepository.findByNegocio_Id(negocio.getId());
    }

    @Transactional
    public Producto crear(Integer idUsuario, ProductoRequest request) {
        Negocio negocio = negocioService.obtenerPorUsuario(idUsuario);
        Categoria categoria = resolverCategoria(negocio.getId(), request.getCategoriaId());
        Seccion seccion = resolverSeccion(negocio.getId(), request.getSeccionId());

        Producto producto = Producto.builder()
                .negocio(negocio)
                .categoria(categoria)
                .seccion(seccion)
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .precio(request.getPrecio())
                .precioComparacion(request.getPrecioComparacion())
                .stock(request.getStock())
                .activo(request.getActivo())
                .destacado(Boolean.TRUE.equals(request.getDestacado()))
                .emoji(request.getEmoji())
                .color(request.getColor())
                .imagenUrl(request.getImagenUrl())
                .build();

        return productoRepository.save(producto);
    }

    @Transactional
    public Producto actualizar(Integer idUsuario, Long productoId, ProductoRequest request) {
        Negocio negocio = negocioService.obtenerPorUsuario(idUsuario);
        Producto producto = productoRepository.findByIdAndNegocio_Id(productoId, negocio.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        Categoria categoria = resolverCategoria(negocio.getId(), request.getCategoriaId());
        Seccion seccion = resolverSeccion(negocio.getId(), request.getSeccionId());

        producto.setCategoria(categoria);
        producto.setSeccion(seccion);
        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecio(request.getPrecio());
        producto.setPrecioComparacion(request.getPrecioComparacion());
        producto.setStock(request.getStock());
        producto.setActivo(request.getActivo());
        producto.setDestacado(Boolean.TRUE.equals(request.getDestacado()));
        producto.setEmoji(request.getEmoji());
        producto.setColor(request.getColor());
        producto.setImagenUrl(request.getImagenUrl());

        return producto;
    }

    @Transactional
    public void eliminar(Integer idUsuario, Long productoId) {
        Negocio negocio = negocioService.obtenerPorUsuario(idUsuario);
        Producto producto = productoRepository.findByIdAndNegocio_Id(productoId, negocio.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        productoRepository.delete(producto);
    }

    private Categoria resolverCategoria(Long negocioId, Long categoriaId) {
        if (categoriaId == null) {
            return null;
        }

        return categoriaRepository.findByIdAndNegocio_Id(categoriaId, negocioId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
    }

    private Seccion resolverSeccion(Long negocioId, Long seccionId) {
        if (seccionId == null) {
            return null;
        }

        return seccionRepository.findByIdAndNegocio_Id(seccionId, negocioId)
                .orElseThrow(() -> new ResourceNotFoundException("Sección no encontrada"));
    }
}
