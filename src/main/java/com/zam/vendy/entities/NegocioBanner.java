package com.zam.vendy.entities;

import com.zam.vendy.entities.enums.Plantilla;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Foto elegida a mano por el negocio para un espacio (slot) puntual de una plantilla de
 * catálogo (ej. una diapositiva del carrusel de portada, una celda del mosaico de Moda).
 * El slot es una clave estable definida por el frontend (ej. "home-hero-1"); mientras el
 * negocio no suba nada para un slot, esa sección sigue mostrando su foto automática de
 * siempre (producto destacado/en oferta) — este registro es solo la anulación explícita.
 *
 * "plantilla" guarda el progreso por separado para cada plantilla (ej. las fotos que subió
 * en Moda no se ven en Accesorios y viceversa) — null para slots que no son de un "Home"
 * de una plantilla puntual (ej. el mensaje del topbar), que se comparten sin importar cuál
 * esté activa. Ver NegocioBannerService/NegocioTextoService, que deciden cuándo va null.
 */
@Entity
@Table(name = "negocio_banner", uniqueConstraints = @UniqueConstraint(columnNames = { "negocio_id", "plantilla", "slot" }))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class NegocioBanner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private Long id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "negocio_id", nullable = false)
    private Negocio negocio;

    @Column(name = "slot", nullable = false, length = 64)
    private String slot;

    @Enumerated(EnumType.STRING)
    @Column(name = "plantilla")
    private Plantilla plantilla;

    @Column(name = "imagen_url", nullable = false, length = 500)
    private String imagenUrl;
}
