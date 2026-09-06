package com.zam.vendy.exceptions;

public class PestanaNoVaciaException extends RuntimeException {

    public PestanaNoVaciaException(String nombre) {
        super("La pestaña '" + nombre + "' todavía tiene secciones — muévelas o elimínalas primero");
    }
}
