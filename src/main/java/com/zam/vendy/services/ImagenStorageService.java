package com.zam.vendy.services;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.zam.vendy.exceptions.ArchivoInvalidoException;

import lombok.RequiredArgsConstructor;

/**
 * Sube archivos a Firebase Storage tal cual llegan (sin comprimir ni recodificar,
 * a pedido explícito) y arma el link público de descarga con el mismo formato que
 * usan los SDKs de Firebase (firebaseStorageDownloadTokens).
 */
@Service
@RequiredArgsConstructor
public class ImagenStorageService {

    private static final Set<String> TIPOS_PERMITIDOS = Set.of(
            "image/jpeg", "image/png", "image/webp", "image/gif", "image/avif");

    private final Storage storage;

    @Value("${firebase.storage-bucket}")
    private String bucket;

    public String subirImagenProducto(MultipartFile archivo) {
        return subirImagen(archivo, "productos");
    }

    public String subirLogoNegocio(MultipartFile archivo) {
        return subirImagen(archivo, "negocios");
    }

    private String subirImagen(MultipartFile archivo, String carpeta) {
        validar(archivo);

        String extension = extensionDe(archivo.getOriginalFilename());
        String ruta = carpeta + "/" + UUID.randomUUID() + extension;
        String token = UUID.randomUUID().toString();

        BlobInfo blobInfo = BlobInfo.newBuilder(bucket, ruta)
                .setContentType(archivo.getContentType())
                .setMetadata(Map.of("firebaseStorageDownloadTokens", token))
                .build();

        try {
            storage.create(blobInfo, archivo.getBytes());
        } catch (IOException e) {
            throw new ArchivoInvalidoException("No se pudo leer el archivo");
        }

        return construirUrlPublica(ruta, token);
    }

    private void validar(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            throw new ArchivoInvalidoException("El archivo está vacío");
        }
        String tipo = archivo.getContentType();
        if (tipo == null || !TIPOS_PERMITIDOS.contains(tipo.toLowerCase())) {
            throw new ArchivoInvalidoException("Solo se permiten imágenes (JPG, PNG, WEBP, GIF, AVIF)");
        }
    }

    private String extensionDe(String nombreOriginal) {
        if (nombreOriginal == null || !nombreOriginal.contains(".")) {
            return "";
        }
        return nombreOriginal.substring(nombreOriginal.lastIndexOf('.')).toLowerCase();
    }

    private String construirUrlPublica(String ruta, String token) {
        String rutaCodificada = URLEncoder.encode(ruta, StandardCharsets.UTF_8);
        return "https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media&token=%s"
                .formatted(bucket, rutaCodificada, token);
    }
}
