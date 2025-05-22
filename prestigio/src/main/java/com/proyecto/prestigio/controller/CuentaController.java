package com.proyecto.prestigio.controller;

import com.proyecto.prestigio.model.Usuario;
import com.proyecto.prestigio.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;


@Controller
public class CuentaController {

    private final UsuarioRepository usuarioRepository;

    public CuentaController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/mi-cuenta")
    public String verMiCuenta(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            String email = auth.getName();
            Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);
            if (usuario != null) {
                model.addAttribute("usuario", usuario);
            }
        }
        return "mi-cuenta"; // Vista Thymeleaf
    }
}
