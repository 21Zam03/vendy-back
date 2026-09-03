package com.zam.vendy.dtos.catalogo;

import java.time.LocalDateTime;
import java.util.List;

import com.zam.vendy.dtos.producto.ProductoResponse;
import com.zam.vendy.entities.Catalogo;
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
public class CatalogoResponse {

    private Long id;
    private String nombre;
    private String slug;
    private Boolean activo;
    private AccentColor accentColor;
    private Background background;
    private Font font;
    private Radius radius;
    private Cover cover;
    private String coverImageUrl;
    private CatalogLayout catalogLayout;
    private List<ProductoResponse> productos;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CatalogoResponse from(Catalogo catalogo) {
        Apariencia apariencia = catalogo.getApariencia();

        return CatalogoResponse.builder()
                .id(catalogo.getId())
                .nombre(catalogo.getNombre())
                .slug(catalogo.getSlug())
                .activo(catalogo.getActivo())
                .accentColor(apariencia != null ? apariencia.getAccentColor() : null)
                .background(apariencia != null ? apariencia.getBackground() : null)
                .font(apariencia != null ? apariencia.getFont() : null)
                .radius(apariencia != null ? apariencia.getRadius() : null)
                .cover(apariencia != null ? apariencia.getCover() : null)
                .coverImageUrl(apariencia != null ? apariencia.getCoverImageUrl() : null)
                .catalogLayout(apariencia != null ? apariencia.getCatalogLayout() : null)
                .productos(catalogo.getProductos().stream().map(ProductoResponse::from).toList())
                .createdAt(catalogo.getCreatedAt())
                .updatedAt(catalogo.getUpdatedAt())
                .build();
    }
}
