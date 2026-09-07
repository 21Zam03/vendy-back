package com.zam.vendy.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
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

// Qué Membresia tiene activa cada Negocio, con historial (cada cambio de plan crea una
// fila nueva en vez de sobreescribir la anterior). Solo una fila por negocio debe tener
// "activa = true" a la vez — lo garantiza SuscripcionService, no una restricción de la
// base de datos. El equipo activa un cambio de plan acá (desactivando la vieja y creando
// la nueva), ya no editando una columna suelta en Negocio.
@Entity
@Table(name = "suscripcion")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Suscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private Long id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "negocio_id", nullable = false)
    private Negocio negocio;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "membresia_id", nullable = false)
    private Membresia membresia;

    @Column(name = "activa", nullable = false)
    @Builder.Default
    private Boolean activa = true;

    // Null = sin vencimiento (ej. Gratis, o un plan pactado sin fecha de corte todavía).
    // La carga el equipo a mano según lo que se haya acordado con el negocio — no se
    // calcula sola a partir de una duración fija, porque no todos los planes/negociaciones
    // duran lo mismo.
    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
