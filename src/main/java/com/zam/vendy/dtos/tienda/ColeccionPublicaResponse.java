package com.zam.vendy.dtos.tienda;

import java.util.List;
import java.util.Objects;

import com.zam.vendy.dtos.categoria.CategoriaResponse;
import com.zam.vendy.entities.Catalogo;
import com.zam.vendy.entities.Producto;
import com.zam.vendy.entities.embeddable.Apariencia;
import com.zam.vendy.entities.enums.AccentColor;
import com.zam.vendy.entities.enums.Background;
import com.zam.vendy.entities.enums.CatalogLayout;
import com.zam.vendy.entities.enums.Cover;
import com.zam.vendy.entities.enums.Font;
import com.zam.vendy.entities.enums.Radius;

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
public class ColeccionPublicaResponse {

    private String nombre;
    private String slug;
    private AccentColor accentColor;
    private Background background;
    private Font font;
    private Radius radius;
    private Cover cover;
    private String coverImageUrl;
    private CatalogLayout catalogLayout;
    private List<CategoriaResponse> categorias;
    private List<ProductoPublicoResponse> productos;

    public static ColeccionPublicaResponse from(Catalogo catalogo) {
        Apariencia apariencia = catalogo.getApariencia();

        List<Producto> productosActivos = catalogo.getProductos().stream()
                .filter(Producto::getActivo)
                .toList();

        List<CategoriaResponse> categorias = productosActivos.stream()
                .map(Producto::getCategoria)
                .filter(Objects::nonNull)
                .distinct()
                .map(CategoriaResponse::from)
                .toList();

        return ColeccionPublicaResponse.builder()
                .nombre(catalogo.getNombre())
                .slug(catalogo.getSlug())
                .accentColor(apariencia != null ? apariencia.getAccentColor() : null)
                .background(apariencia != null ? apariencia.getBackground() : null)
                .font(apariencia != null ? apariencia.getFont() : null)
                .radius(apariencia != null ? apariencia.getRadius() : null)
                .cover(apariencia != null ? apariencia.getCover() : null)
                .coverImageUrl(apariencia != null ? apariencia.getCoverImageUrl() : null)
                .catalogLayout(apariencia != null ? apariencia.getCatalogLayout() : null)
                .categorias(categorias)
                .productos(productosActivos.stream().map(ProductoPublicoResponse::from).toList())
                .build();
    }
}
