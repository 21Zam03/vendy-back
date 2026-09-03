package com.zam.vendy.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "tb_empresa")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_empresa")
    @EqualsAndHashCode.Include
    private Long idEmpresa;

    @Column(name = "ruc")
    private String ruc;

    @Column(name = "razon_social")
    private String razonSocial;

    @Column(name = "nombre_comercial")
    private String nombreComercial;

    @Column(name = "direccion")
    private String direccion;

    @Column(name = "correo")
    private String correo;

    @Column(name = "numero_telefono")
    private String numeroTelefono;

    @Column(name = "ruta")
    private String ruta;

    @Column(name = "url_imagen")
    private String urlImagen;

    @Column(name = "id_agente_pordefecto")
    private Long idAgentePordefecto;

    @Column(name = "tiene_guardado_automatico")
    private Boolean tieneGuardadoAutomatico;

    @Column(name = "tiene_codigo_barras")
    private Boolean tieneCodigoBarras;

    @Column(name = "tiene_impresora")
    private Boolean tieneImpresora;

    @Column(name = "tiene_stock")
    private Boolean tieneStock;

    @Column(name = "es_activa")
    private Boolean esActiva;

    @Column(name = "impuesto_visible")
    private Boolean impuestoVisible;

    @Column(name = "es_prueba")
    private Boolean esPrueba;

    @Column(name = "igv_incluido")
    private Boolean igvIncluido;

    @ToString.Exclude
    @Builder.Default
    @OneToMany(mappedBy = "empresa", fetch = FetchType.LAZY)
    private List<Cliente> clientes = new ArrayList<>();

    @ToString.Exclude
    @Builder.Default
    @OneToMany(mappedBy = "empresa", fetch = FetchType.LAZY)
    private List<Usuario> usuarios = new ArrayList<>();
}
