package com.zam.vendy.entities.embeddable;

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
public class RedesSociales {

    @Column(name = "instagram")
    private String instagram;

    @Column(name = "tiktok")
    private String tiktok;

    @Column(name = "facebook")
    private String facebook;
}
