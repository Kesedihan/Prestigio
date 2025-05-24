package com.proyecto.prestigio.controller;

import com.proyecto.prestigio.model.Usuario;
import com.proyecto.prestigio.service.VentaService;
import com.proyecto.prestigio.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/mi-cuenta")
public class HistorialController {

    private final VentaService ventaService;
    private final UsuarioRepository usuarioRepository;

    public HistorialController(VentaService ventaService, UsuarioRepository usuarioRepository) {
        this.ventaService = ventaService;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/historial")
    public String verHistorial(Model model) {
        Usuario usuario = getUsuarioActual();
        model.addAttribute("ventas", ventaService.obtenerHistorialPorUsuario(usuario));
        return "historial"; // crea la vista historial.html
    }

    private Usuario getUsuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return usuarioRepository.findByEmail(email).orElseThrow();
    }
}
