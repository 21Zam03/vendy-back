package com.zam.vendy.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Catálogo de planes (Gratis/Go/Premium) — antes vivía hardcodeado como un enum + los
// datos de comparación en el frontend (src/data/plans.js). Ahora es una tabla real: los
// límites (ej. productos) se leen de acá en vez de estar fijos en el código, y qué plan
// tiene cada negocio se resuelve por su Suscripcion activa, no por una columna suelta.
// Se siembra sola al arrancar el backend (ver MembresiaSeeder) — no se crea a mano.
@Entity
@Table(name = "membresia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Membresia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private Long id;

    // Coincide con el nombre del enum Plan ("GRATIS", "GO", "PREMIUM") — es el puente
    // entre el catálogo en base de datos y las constantes usadas en el código.
    @Column(name = "clave", nullable = false, unique = true, length = 20)
    private String clave;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    // De menor a mayor beneficio — permite comparar "requiere al menos este nivel" sin
    // depender del orden en que se insertaron las filas.
    @Column(name = "nivel", nullable = false)
    private Integer nivel;

    // Null = sin límite (Go y Premium).
    @Column(name = "limite_productos")
    private Integer limiteProductos;
}
