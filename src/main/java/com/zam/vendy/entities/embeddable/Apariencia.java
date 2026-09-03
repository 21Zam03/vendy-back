package com.zam.vendy.entities.embeddable;

import com.zam.vendy.entities.enums.AccentColor;
import com.zam.vendy.entities.enums.Background;
import com.zam.vendy.entities.enums.CatalogLayout;
import com.zam.vendy.entities.enums.Cover;
import com.zam.vendy.entities.enums.Font;
import com.zam.vendy.entities.enums.Radius;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class Apariencia {

    @Column(name = "accent_color", nullable = false)
    private AccentColor accentColor;

    @Column(name = "background", nullable = false)
    private Background background;

    @Column(name = "font", nullable = false)
    private Font font;

    @Column(name = "radius", nullable = false)
    private Radius radius;

    @Column(name = "cover", nullable = false)
    private Cover cover;

    // Nullable a propósito: se agregó después de accentColor/background/font/radius/cover,
    // así que negocios ya guardados no van a tener valor hasta que lo actualicen.
    @Column(name = "catalog_layout")
    private CatalogLayout catalogLayout;
}
