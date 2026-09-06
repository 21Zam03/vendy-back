package com.zam.vendy.dtos.negocio;

import java.util.List;

import com.zam.vendy.entities.enums.AccentColor;
import com.zam.vendy.entities.enums.Background;
import com.zam.vendy.entities.enums.CatalogLayout;
import com.zam.vendy.entities.enums.Cover;
import com.zam.vendy.entities.enums.Font;
import com.zam.vendy.entities.enums.Plantilla;
import com.zam.vendy.entities.enums.Radius;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NegocioUpdateRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El slug es obligatorio")
    @Pattern(regexp = "^[a-z0-9]+(-[a-z0-9]+)*$",
            message = "El slug solo puede contener minúsculas, números y guiones")
    private String slug;

    private String descripcion;

    private String whatsapp;

    private String ubicacion;

    private String horario;

    @Size(max = 2, message = "logoInitials admite máximo 2 caracteres")
    private String logoInitials;

    private String logoUrl;

    private String instagram;

    private String tiktok;

    private String facebook;

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

    private String coverImageUrl;

    private CatalogLayout catalogLayout;

    private Plantilla plantilla;

    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "accentColorHex debe ser un color hexadecimal, ej. #5a32f4")
    private String accentColorHex;

    private String backgroundImageUrl;

    private List<MetodoPagoRequest> metodosPago;
}
