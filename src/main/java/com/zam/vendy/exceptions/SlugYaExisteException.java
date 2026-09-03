package com.zam.vendy.exceptions;

public class SlugYaExisteException extends RuntimeException {

    public SlugYaExisteException(String slug) {
        super("El slug '" + slug + "' ya está en uso");
    }
}
