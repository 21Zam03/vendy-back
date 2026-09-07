package com.zam.vendy.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.zam.vendy.entities.ArchivoNegocio;
import com.zam.vendy.entities.Negocio;
import com.zam.vendy.exceptions.ResourceNotFoundException;
import com.zam.vendy.repositories.ArchivoNegocioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ArchivoService {

    private final ArchivoNegocioRepository archivoNegocioRepository;
    private final NegocioService negocioService;
    private final ImagenStorageService imagenStorageService;

    @Transactional(readOnly = true)
    public List<ArchivoNegocio> listar(Integer idUsuario) {
        Negocio negocio = negocioService.obtenerPorUsuario(idUsuario);
        return archivoNegocioRepository.findByNegocio_IdOrderByCreatedAtDesc(negocio.getId());
    }

    // Sube el archivo a Firebase Storage y lo registra en la biblioteca en un solo paso —
    // así toda foto subida desde cualquier parte del catálogo que use este endpoint queda
    // disponible para reusar después, sin un paso aparte de "guardar en mi biblioteca".
    @Transactional
    public ArchivoNegocio subir(Integer idUsuario, MultipartFile archivo) {
        Negocio negocio = negocioService.obtenerPorUsuario(idUsuario);
        String url = imagenStorageService.subirArchivoNegocio(archivo);

        ArchivoNegocio item = ArchivoNegocio.builder()
                .negocio(negocio)
                .url(url)
                .nombre(archivo.getOriginalFilename())
                .build();
        return archivoNegocioRepository.save(item);
    }

    @Transactional
    public void eliminar(Integer idUsuario, Long archivoId) {
        Negocio negocio = negocioService.obtenerPorUsuario(idUsuario);
        ArchivoNegocio archivo = archivoNegocioRepository.findByIdAndNegocio_Id(archivoId, negocio.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Archivo no encontrado"));
        archivoNegocioRepository.delete(archivo);
    }
}
