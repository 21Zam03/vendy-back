package com.zam.vendy.entities.embeddable;

import com.zam.vendy.entities.enums.AccentColor;
import com.zam.vendy.entities.enums.Background;
import com.zam.vendy.entities.enums.CatalogLayout;
import com.zam.vendy.entities.enums.Cover;
import com.zam.vendy.entities.enums.Font;
import com.zam.vendy.entities.enums.Plantilla;
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

    // Solo se usa cuando cover = IMAGEN; en solid/gradient queda null.
    @Column(name = "cover_image_url", length = 1000)
    private String coverImageUrl;

    // Nullable: null significa "sin plantilla elegida", en cuyo caso la página pública
    // usa la composición genérica de siempre (sin romper negocios ya existentes).
    @Column(name = "plantilla")
    private Plantilla plantilla;

    // Solo se usa cuando accentColor = CUSTOM; en cualquier otro caso queda null.
    @Column(name = "accent_color_hex", length = 9)
    private String accentColorHex;

    // Solo se usa cuando background = IMAGEN; en el resto de fondos queda null.
    @Column(name = "background_image_url", length = 1000)
    private String backgroundImageUrl;
}
