package com.zam.vendy.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.zam.vendy.entities.embeddable.Apariencia;
import com.zam.vendy.entities.embeddable.MetodoPago;
import com.zam.vendy.entities.embeddable.RedesSociales;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "negocio")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Negocio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private Long id;

    @ToString.Exclude
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "slug", nullable = false, unique = true)
    private String slug;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "whatsapp")
    private String whatsapp;

    @Column(name = "ubicacion")
    private String ubicacion;

    @Column(name = "logo_initials", length = 2)
    private String logoInitials;

    // URL pública de la foto de perfil en Firebase Storage; si no hay, se sigue
    // usando logoInitials como respaldo (círculo con iniciales).
    @Column(name = "logo_url", length = 1000)
    private String logoUrl;

    @Embedded
    private RedesSociales redesSociales;

    @Embedded
    private Apariencia apariencia;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ToString.Exclude
    @Builder.Default
    @OneToMany(mappedBy = "negocio", fetch = FetchType.LAZY)
    private List<Categoria> categorias = new ArrayList<>();

    @ToString.Exclude
    @Builder.Default
    @OneToMany(mappedBy = "negocio", fetch = FetchType.LAZY)
    private List<Producto> productos = new ArrayList<>();

    @ToString.Exclude
    @Builder.Default
    @OneToMany(mappedBy = "negocio", fetch = FetchType.LAZY)
    private List<EnlaceNegocio> enlaces = new ArrayList<>();

    // Nueva tabla (distinta de la vieja "negocio_metodo_pago", que solo guardaba el
    // nombre del método sin detalle) para poder agregar el número/cuenta de cada uno.
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "negocio_metodo_pago_info", joinColumns = @JoinColumn(name = "negocio_id"))
    @Builder.Default
    private List<MetodoPago> metodosPago = new ArrayList<>();
}
