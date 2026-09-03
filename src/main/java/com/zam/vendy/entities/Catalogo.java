package com.zam.vendy.entities;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.zam.vendy.entities.embeddable.Apariencia;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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
 * Una "colección" temática dentro del negocio (ej. Catálogo de Navidad, Catálogo de
 * Halloween): un subconjunto curado de productos, con su propia apariencia y su propio
 * link público, independiente del catálogo general del negocio.
 */
@Entity
@Table(name = "catalogo", uniqueConstraints = @UniqueConstraint(columnNames = { "negocio_id", "slug" }))
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Catalogo {

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

    @Column(name = "slug", nullable = false)
    private String slug;

    @Column(name = "activo", nullable = false)
    @Builder.Default
    private Boolean activo = Boolean.TRUE;

    @Embedded
    private Apariencia apariencia;

    // EAGER a propósito: CatalogoResponse.from() lee esta colección fuera de la
    // transacción del service (en el controller), así que no puede ser LAZY.
    @ToString.Exclude
    @Builder.Default
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "catalogo_producto",
            joinColumns = @JoinColumn(name = "catalogo_id"),
            inverseJoinColumns = @JoinColumn(name = "producto_id"))
    private Set<Producto> productos = new HashSet<>();

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
