package com.zam.vendy.dtos.producto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.zam.vendy.entities.Categoria;
import com.zam.vendy.entities.Producto;
import com.zam.vendy.entities.Seccion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoResponse {

    private Long id;
    private Long categoriaId;
    private String categoriaNombre;
    private Long seccionId;
    private String seccionNombre;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private BigDecimal precioComparacion;
    private Integer stock;
    private Boolean activo;
    private Boolean destacado;
    private String emoji;
    private String color;
    private String imagenUrl;
    private Integer orden;
    private Long vistas;
    private Long consultas;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ProductoResponse from(Producto producto) {
        Categoria categoria = producto.getCategoria();
        Seccion seccion = producto.getSeccion();

        return ProductoResponse.builder()
                .id(producto.getId())
                .categoriaId(categoria != null ? categoria.getId() : null)
                .categoriaNombre(categoria != null ? categoria.getNombre() : null)
                .seccionId(seccion != null ? seccion.getId() : null)
                .seccionNombre(seccion != null ? seccion.getNombre() : null)
                .nombre(producto.getNombre())
                .descripcion(producto.getDescripcion())
                .precio(producto.getPrecio())
                .precioComparacion(producto.getPrecioComparacion())
                .stock(producto.getStock())
                .activo(producto.getActivo())
                .destacado(producto.getDestacado())
                .emoji(producto.getEmoji())
                .color(producto.getColor())
                .imagenUrl(producto.getImagenUrl())
                .orden(producto.getOrden())
                .vistas(producto.getVistas())
                .consultas(producto.getConsultas())
                .createdAt(producto.getCreatedAt())
                .updatedAt(producto.getUpdatedAt())
                .build();
    }
}
