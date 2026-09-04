package com.finanzas.controller;

import com.finanzas.config.JwtUtil;
import com.finanzas.model.Usuario;
import com.finanzas.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/registro")
    public Usuario registrar(@RequestBody Usuario usuario) {
        return usuarioService.guardar(usuario);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody Map<String, String> body) {
        Usuario usuario = usuarioService.login(body.get("email"), body.get("password"));
        String token = jwtUtil.generarToken(usuario.getEmail());
        return LoginResponse.desde(usuario, token);
    }
}
