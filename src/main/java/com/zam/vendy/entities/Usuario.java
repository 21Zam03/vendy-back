package com.zam.vendy.entities;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.zam.vendy.entities.enums.ProveedorAuth;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
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
@Table(name = "tb_usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    @EqualsAndHashCode.Include
    private Integer idUsuario;

    @Column(name = "nombre_usuario", unique = true)
    private String nombreUsuario;

    @ToString.Exclude
    @Column(name = "contrasena", length = 100)
    private String contrasena;

    @Column(name = "nombres")
    private String nombres;

    @Column(name = "apellidos")
    private String apellidos;

    @Column(name = "alias")
    private String alias;

    @Column(name = "correo", length = 50)
    private String correo;

    // Cuentas creadas por "Continuar con Google" no tienen contraseña (queda null):
    // googleId es el "sub" del token de Google, único por cuenta de Google.
    @Column(name = "google_id", unique = true)
    private String googleId;

    @Enumerated(EnumType.STRING)
    @Column(name = "proveedor")
    @Builder.Default
    private ProveedorAuth proveedor = ProveedorAuth.LOCAL;

    @Column(name = "activo")
    private Boolean activo;

    @Column(name = "cuenta_bloqueada")
    private Boolean cuentaBloqueada;

    @Column(name = "cuenta_expirada")
    private Boolean cuentaExpirada;

    @Column(name = "credenciales_expiradas")
    private Boolean credencialesExpiradas;

    @Column(name = "reseteo_contrasena")
    private Boolean reseteoContrasena;

    @Column(name = "fecha_actualizacion_contrasena")
    private LocalDateTime fechaActualizacionContrasena;

    @Column(name = "creado_por")
    private Long creadoPor;

    @Column(name = "actualizado_por")
    private Long actualizadoPor;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empresa")
    private Empresa empresa;

    @ToString.Exclude
    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "tb_usuarios_roles",
            joinColumns = @JoinColumn(name = "id_usuario"),
            inverseJoinColumns = @JoinColumn(name = "id_rol")
    )
    private Set<Rol> roles = new HashSet<>();

    @ToString.Exclude
    @OneToOne(mappedBy = "usuario", fetch = FetchType.LAZY)
    private Negocio negocio;
}
