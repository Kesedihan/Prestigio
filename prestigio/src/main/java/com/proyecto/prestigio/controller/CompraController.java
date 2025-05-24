package com.proyecto.prestigio.controller;

import com.proyecto.prestigio.model.Usuario;
import com.proyecto.prestigio.service.VentaService;
import com.proyecto.prestigio.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/comprar")
public class CompraController {

    private final VentaService ventaService;
    private final UsuarioRepository usuarioRepository;

    public CompraController(VentaService ventaService, UsuarioRepository usuarioRepository) {
        this.ventaService = ventaService;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping
    public String comprar(@RequestParam String direccion, @RequestParam String tipoPago) {
        Usuario usuario = getUsuarioActual();
        ventaService.registrarCompra(usuario, direccion, tipoPago);
        return "redirect:/mi-cuenta?compraExitosa";
    }

    private Usuario getUsuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return usuarioRepository.findByEmail(email).orElseThrow();
    }
}