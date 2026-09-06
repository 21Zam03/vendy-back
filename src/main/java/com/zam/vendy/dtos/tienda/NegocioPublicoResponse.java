package com.zam.vendy.dtos.tienda;

import java.util.List;

import com.zam.vendy.dtos.enlace.EnlaceResponse;
import com.zam.vendy.dtos.negocio.MetodoPagoResponse;
import com.zam.vendy.entities.EnlaceNegocio;
import com.zam.vendy.entities.Negocio;
import com.zam.vendy.entities.embeddable.Apariencia;
import com.zam.vendy.entities.embeddable.RedesSociales;
import com.zam.vendy.entities.enums.AccentColor;
import com.zam.vendy.entities.enums.Background;
import com.zam.vendy.entities.enums.CatalogLayout;
import com.zam.vendy.entities.enums.Cover;
import com.zam.vendy.entities.enums.Font;
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
public class NegocioPublicoResponse {

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
    private List<EnlaceResponse> enlaces;

    public static NegocioPublicoResponse from(Negocio negocio, List<EnlaceNegocio> enlaces) {
        RedesSociales redes = negocio.getRedesSociales();
        Apariencia apariencia = negocio.getApariencia();

        return NegocioPublicoResponse.builder()
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
                .enlaces(enlaces.stream().map(EnlaceResponse::from).toList())
                .build();
    }
}
