package com.sistema.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta retornado tras un login o registro exitoso.
 * Contiene el token JWT y la información básica del usuario.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String email;
    private String rol;
    private long expiraEn; // milisegundos hasta que expira el token
}
