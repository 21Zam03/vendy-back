package com.zam.vendy.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Agrupa Secciones dentro del catálogo (ej. "Hombre" y "Mujer" como pestañas, cada una
 * con sus propias secciones "Novedades", "Ofertas"). Toda Sección pertenece a exactamente
 * una Pestaña — es el primer nivel de la estructura del catálogo, definido libremente por
 * el negocio, y se muestran como pestañas navegables en el catálogo público.
 */
@Entity
@Table(name = "pestana")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Pestana {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private Long id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "negocio_id", nullable = false)
    private Negocio negocio;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "orden", nullable = false)
    @Builder.Default
    private Integer orden = 0;

    // Marca la pestaña que arma la portada especial de Moda (carrusel, mosaico de
    // categorías, etc.) — se fija una sola vez al crearla desde la plantilla y es
    // independiente de "nombre", para que el negocio pueda renombrarla libremente (ej.
    // "Home" -> "Inicio") sin perder esa estructura. Nullable a propósito: las pestañas
    // creadas antes de que existiera este campo lo tienen en null hasta que se autocuran
    // (ver PestanaService.asegurarSinHuerfanas).
    @Column(name = "es_home")
    @Builder.Default
    private Boolean esHome = false;

    // Marca la pestaña "General" que autocura Secciones sin pestaña (ver
    // PestanaService.asegurarSinHuerfanas) — representa el catálogo completo del negocio
    // (todos los productos registrados), y el frontend la muestra con una grilla de
    // catálogo simple en vez del estilo decorativo de la plantilla. Mismo criterio que
    // esHome: independiente de "nombre", nullable a propósito para autocurar pestañas
    // "General" creadas antes de que existiera este campo.
    @Column(name = "es_general")
    @Builder.Default
    private Boolean esGeneral = false;

    // Si el negocio la desactiva, deja de listarse en el catálogo público (pero sigue
    // existiendo, con sus secciones/productos intactos) — puede reactivarla cuando
    // quiera (ver PestanaService.cambiarActiva). Nullable a propósito: pestañas creadas
    // antes de que existiera este campo quedan en null, que se trata como "activa" en
    // todos lados (nunca deben desaparecer solas del catálogo por una migración de
    // columna) — ver PestanaResponse.from y CatalogTemplateRenderer.vue.
    @Column(name = "activa")
    @Builder.Default
    private Boolean activa = true;
}
