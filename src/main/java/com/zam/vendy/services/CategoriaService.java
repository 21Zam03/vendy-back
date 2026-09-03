package com.zam.vendy.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zam.vendy.dtos.categoria.CategoriaRequest;
import com.zam.vendy.entities.Categoria;
import com.zam.vendy.entities.Negocio;
import com.zam.vendy.exceptions.ResourceNotFoundException;
import com.zam.vendy.repositories.CategoriaRepository;
import com.zam.vendy.repositories.ProductoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;
    private final NegocioService negocioService;

    @Transactional(readOnly = true)
    public List<Categoria> listar(Integer idUsuario) {
        Negocio negocio = negocioService.obtenerPorUsuario(idUsuario);
        return categoriaRepository.findByNegocio_IdOrderByNombreAsc(negocio.getId());
    }

    @Transactional
    public Categoria crear(Integer idUsuario, CategoriaRequest request) {
        Negocio negocio = negocioService.obtenerPorUsuario(idUsuario);

        Categoria categoria = Categoria.builder()
                .negocio(negocio)
                .nombre(request.getNombre())
                .emoji(request.getEmoji())
                .build();

        return categoriaRepository.save(categoria);
    }

    @Transactional
    public Categoria actualizar(Integer idUsuario, Long categoriaId, CategoriaRequest request) {
        Categoria categoria = obtenerPropia(idUsuario, categoriaId);

        categoria.setNombre(request.getNombre());
        categoria.setEmoji(request.getEmoji());

        return categoria;
    }

    @Transactional
    public void eliminar(Integer idUsuario, Long categoriaId) {
        Categoria categoria = obtenerPropia(idUsuario, categoriaId);

        productoRepository.desasociarCategoria(categoria.getId());
        categoriaRepository.delete(categoria);
    }

    private Categoria obtenerPropia(Integer idUsuario, Long categoriaId) {
        Negocio negocio = negocioService.obtenerPorUsuario(idUsuario);
        return categoriaRepository.findByIdAndNegocio_Id(categoriaId, negocio.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
    }
}
