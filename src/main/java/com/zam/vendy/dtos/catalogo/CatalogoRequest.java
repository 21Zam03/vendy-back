package com.zam.vendy.dtos.catalogo;

import java.util.List;

import com.zam.vendy.entities.enums.AccentColor;
import com.zam.vendy.entities.enums.Background;
import com.zam.vendy.entities.enums.CatalogLayout;
import com.zam.vendy.entities.enums.Cover;
import com.zam.vendy.entities.enums.Font;
import com.zam.vendy.entities.enums.Radius;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CatalogoRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El slug es obligatorio")
    @Pattern(regexp = "^[a-z0-9]+(-[a-z0-9]+)*$",
            message = "El slug solo puede contener minúsculas, números y guiones")
    private String slug;

    @NotNull(message = "El campo activo es obligatorio")
    private Boolean activo;

    @NotNull(message = "accentColor es obligatorio")
    private AccentColor accentColor;

    @NotNull(message = "background es obligatorio")
    private Background background;

    @NotNull(message = "font es obligatorio")
    private Font font;

    @NotNull(message = "radius es obligatorio")
    private Radius radius;

    @NotNull(message = "cover es obligatorio")
    private Cover cover;

    private CatalogLayout catalogLayout;

    private List<Long> productoIds;
}
