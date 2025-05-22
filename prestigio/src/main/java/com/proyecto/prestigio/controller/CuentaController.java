package com.proyecto.prestigio.controller;

import com.proyecto.prestigio.model.Cita;
import com.proyecto.prestigio.model.Usuario;
import com.proyecto.prestigio.repository.CitaRepository;
import com.proyecto.prestigio.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@Controller
public class CuentaController {

    private final UsuarioRepository usuarioRepository;
    private final CitaRepository citaRepository;

    public CuentaController(UsuarioRepository usuarioRepository, CitaRepository citaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.citaRepository = citaRepository;
    }

    @GetMapping("/mi-cuenta")
    public String verMiCuenta(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            String email = auth.getName();
            Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);
            if (usuario != null) {
                model.addAttribute("usuario", usuario);
                model.addAttribute("citas", citaRepository.findByUsuarioOrderByFechaDesc(usuario));
            }
        }
        return "mi-cuenta";
    }

    @PostMapping("/cliente/cancelar-cita")
    public String cancelarCita(@RequestParam Long citaId) {
        Cita cita = citaRepository.findById(citaId).orElse(null);
        if (cita != null && cita.getEstado().equals("PENDIENTE")) {
            cita.setEstado("CANCELADA");
            citaRepository.save(cita);
        }
        return "redirect:/mi-cuenta";
    }
}
