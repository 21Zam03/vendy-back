package com.zam.vendy.exceptions;

// Se lanza cuando el negocio intenta algo que su plan actual no incluye (límite de
// productos, plantillas de catálogo, o un módulo entero como Colecciones/Estadísticas).
public class LimitePlanExcedidoException extends RuntimeException {

    public LimitePlanExcedidoException(String message) {
        super(message);
    }
}
