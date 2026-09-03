package com.zam.vendy.entities.embeddable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Un método de pago aceptado por el negocio (ej. "yape") junto con el dato de
 * contacto asociado (ej. el número de celular), que se muestra en la página pública.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class MetodoPago {

    @Column(name = "tipo", nullable = false)
    private String tipo;

    @Column(name = "detalle")
    private String detalle;
}
