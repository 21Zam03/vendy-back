package com.zam.vendy.dtos.tienda;

import java.math.BigDecimal;

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
public class ProductoPublicoResponse {

    private Long id;
    private Long categoriaId;
    private Long seccionId;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private BigDecimal precioComparacion;
    private Integer stock;
    private String emoji;
    private String color;
    private String imagenUrl;

    public static ProductoPublicoResponse from(Producto producto) {
        Categoria categoria = producto.getCategoria();
        Seccion seccion = producto.getSeccion();

        return ProductoPublicoResponse.builder()
                .id(producto.getId())
                .categoriaId(categoria != null ? categoria.getId() : null)
                .seccionId(seccion != null ? seccion.getId() : null)
                .nombre(producto.getNombre())
                .descripcion(producto.getDescripcion())
                .precio(producto.getPrecio())
                .precioComparacion(producto.getPrecioComparacion())
                .stock(producto.getStock())
                .emoji(producto.getEmoji())
                .color(producto.getColor())
                .imagenUrl(producto.getImagenUrl())
                .build();
    }
}
