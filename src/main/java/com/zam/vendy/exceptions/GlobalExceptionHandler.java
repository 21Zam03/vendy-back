package com.zam.vendy.exceptions;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import tools.jackson.databind.exc.InvalidFormatException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleResourceNotFound(ResourceNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", exception.getMessage()));
    }

    @ExceptionHandler(ArchivoInvalidoException.class)
    public ResponseEntity<Map<String, String>> handleArchivoInvalido(ArchivoInvalidoException exception) {
        return ResponseEntity.badRequest().body(Map.of("message", exception.getMessage()));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, String>> handleMaxUploadSize(MaxUploadSizeExceededException exception) {
        return ResponseEntity.badRequest().body(Map.of("message", "El archivo es demasiado grande"));
    }

    @ExceptionHandler(SlugYaExisteException.class)
    public ResponseEntity<Map<String, String>> handleSlugYaExiste(SlugYaExisteException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", exception.getMessage()));
    }

    @ExceptionHandler(PestanaEstructuraFijaException.class)
    public ResponseEntity<Map<String, String>> handlePestanaEstructuraFija(PestanaEstructuraFijaException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", exception.getMessage()));
    }

    @ExceptionHandler(LimitePlanExcedidoException.class)
    public ResponseEntity<Map<String, String>> handleLimitePlan(LimitePlanExcedidoException exception) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException exception) {
        Map<String, String> errores = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors()
                .forEach(error -> errores.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errores);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, String>> handleAuthentication(AuthenticationException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Credenciales inválidas"));
    }

    // Sin este handler, un valor que no existe en un enum (ej. un accentColor/plantilla/etc.
    // que no está en la paleta predefinida) tira una IllegalArgumentException DENTRO del
    // parseo del JSON (ver el @JsonCreator de cada enum en entities/enums) — nunca llega a
    // tocar la base de datos, pero sin manejarla acá Spring la deja caer a su respuesta
    // genérica de error (application/problem+json), que el frontend ni siquiera reconoce
    // como JSON (ver apiFetch en el front) y termina mostrando "no se pudo conectar con el
    // servidor", muy confuso para lo que en realidad es un dato inválido. Acá se identifica
    // el campo real que falló para devolver el mismo formato {campo: mensaje} que ya usan
    // las validaciones de @Valid (ver handleValidation).
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleMessageNotReadable(HttpMessageNotReadableException exception) {
        if (exception.getCause() instanceof InvalidFormatException invalidFormat && !invalidFormat.getPath().isEmpty()) {
            String campo = invalidFormat.getPath().get(0).getPropertyName();
            return ResponseEntity.badRequest()
                    .body(Map.of(campo, "\"" + invalidFormat.getValue() + "\" no es un valor válido para " + campo));
        }
        return ResponseEntity.badRequest().body(Map.of("message", "La solicitud tiene datos con un formato inválido"));
    }
}
