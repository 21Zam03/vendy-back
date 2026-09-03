package com.zam.vendy.dtos.auth;

import java.util.Set;

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
public class UserInfoResponse {

    private Integer idUsuario;
    private String nombreUsuario;
    private String nombres;
    private String apellidos;
    private String correo;
    private Long idEmpresa;
    private Set<String> roles;
}
