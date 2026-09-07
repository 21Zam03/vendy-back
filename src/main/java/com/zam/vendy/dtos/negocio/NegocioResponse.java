package com.zam.vendy.dtos.negocio;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.zam.vendy.entities.Negocio;
import com.zam.vendy.entities.embeddable.Apariencia;
import com.zam.vendy.entities.embeddable.RedesSociales;
import com.zam.vendy.entities.enums.AccentColor;
import com.zam.vendy.entities.enums.Background;
import com.zam.vendy.entities.enums.CatalogLayout;
import com.zam.vendy.entities.enums.Cover;
import com.zam.vendy.entities.enums.Font;
import com.zam.vendy.entities.enums.Plan;
import com.zam.vendy.entities.enums.Plantilla;
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
public class NegocioResponse {

    private Long id;
    private String nombre;
    private String slug;
    private String descripcion;
    private String whatsapp;
    private String ubicacion;
    private String horario;
    private String logoInitials;
    private String logoUrl;
    private String instagram;
    private String tiktok;
    private String facebook;
    private AccentColor accentColor;
    private Background background;
    private Font font;
    private Radius radius;
    private Cover cover;
    private String coverImageUrl;
    private CatalogLayout catalogLayout;
    private Plantilla plantilla;
    private String accentColorHex;
    private String backgroundImageUrl;
    private List<MetodoPagoResponse> metodosPago;
    private Plan plan;
    private LocalDateTime planActivoDesde;
    private LocalDate planVenceEl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // "plan"/"planActivoDesde"/"planVenceEl" se reciben ya resueltos desde la Suscripcion
    // activa (ver SuscripcionService) en vez de leerse directo de Negocio.plan — esa
    // columna quedó como dato viejo desde que el plan se maneja con Suscripcion.
    public static NegocioResponse from(Negocio negocio, Plan plan, LocalDateTime planActivoDesde, LocalDate planVenceEl) {
        RedesSociales redes = negocio.getRedesSociales();
        Apariencia apariencia = negocio.getApariencia();

        return NegocioResponse.builder()
                .id(negocio.getId())
                .plan(plan)
                .planActivoDesde(planActivoDesde)
                .planVenceEl(planVenceEl)
                .nombre(negocio.getNombre())
                .slug(negocio.getSlug())
                .descripcion(negocio.getDescripcion())
                .whatsapp(negocio.getWhatsapp())
                .ubicacion(negocio.getUbicacion())
                .horario(negocio.getHorario())
                .logoInitials(negocio.getLogoInitials())
                .logoUrl(negocio.getLogoUrl())
                .instagram(redes != null ? redes.getInstagram() : null)
                .tiktok(redes != null ? redes.getTiktok() : null)
                .facebook(redes != null ? redes.getFacebook() : null)
                .accentColor(apariencia != null ? apariencia.getAccentColor() : null)
                .background(apariencia != null ? apariencia.getBackground() : null)
                .font(apariencia != null ? apariencia.getFont() : null)
                .radius(apariencia != null ? apariencia.getRadius() : null)
                .cover(apariencia != null ? apariencia.getCover() : null)
                .coverImageUrl(apariencia != null ? apariencia.getCoverImageUrl() : null)
                .catalogLayout(apariencia != null ? apariencia.getCatalogLayout() : null)
                .plantilla(apariencia != null ? apariencia.getPlantilla() : null)
                .accentColorHex(apariencia != null ? apariencia.getAccentColorHex() : null)
                .backgroundImageUrl(apariencia != null ? apariencia.getBackgroundImageUrl() : null)
                .metodosPago(negocio.getMetodosPago().stream().map(MetodoPagoResponse::from).toList())
                .createdAt(negocio.getCreatedAt())
                .updatedAt(negocio.getUpdatedAt())
                .build();
    }
}
