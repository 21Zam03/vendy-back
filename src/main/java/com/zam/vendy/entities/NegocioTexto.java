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
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Texto elegido a mano por el negocio para un espacio puntual de una plantilla de
 * catálogo (ej. el título de una sección de Home en Moda). El slot es una clave estable
 * definida por el frontend (ej. "home-title-keyitems"); mientras el negocio no escriba
 * nada para un slot, esa sección simplemente no tiene título — nunca hay un texto por
 * defecto inventado (nombre de categoría, de producto, etc.).
 */
@Entity
@Table(name = "negocio_texto", uniqueConstraints = @UniqueConstraint(columnNames = { "negocio_id", "slot" }))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class NegocioTexto {

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

    @Column(name = "texto", nullable = false, length = 255)
    private String texto;
}
