package com.finanzas.controller;

import com.finanzas.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private Long id;
    private String nombre;
    private String email;
    private String token;

    public static LoginResponse desde(Usuario usuario, String token) {
        return new LoginResponse(usuario.getId(), usuario.getNombre(), usuario.getEmail(), token);
    }
}
