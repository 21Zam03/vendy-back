package com.zam.vendy.dtos.enlace;

import com.zam.vendy.entities.EnlaceNegocio;

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
public class EnlaceResponse {

    private Long id;
    private String label;
    private String url;

    public static EnlaceResponse from(EnlaceNegocio enlace) {
        return EnlaceResponse.builder()
                .id(enlace.getId())
                .label(enlace.getLabel())
                .url(enlace.getUrl())
                .build();
    }
}
