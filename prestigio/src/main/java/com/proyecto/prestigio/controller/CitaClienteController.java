package com.proyecto.prestigio.controller;

import com.proyecto.prestigio.model.Cita;
import com.proyecto.prestigio.model.Disponibilidad;
import com.proyecto.prestigio.model.Usuario;
import com.proyecto.prestigio.repository.DisponibilidadRepository;
import com.proyecto.prestigio.repository.UsuarioRepository;
import com.proyecto.prestigio.repository.CitaRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/cliente")
public class CitaClienteController {

    private final DisponibilidadRepository disponibilidadRepository;
    private final UsuarioRepository usuarioRepository;
    private final CitaRepository citaRepository;

    public CitaClienteController(DisponibilidadRepository disponibilidadRepository,
                                 UsuarioRepository usuarioRepository,
                                 CitaRepository citaRepository) {
        this.disponibilidadRepository = disponibilidadRepository;
        this.usuarioRepository = usuarioRepository;
        this.citaRepository = citaRepository;
    }

    @GetMapping("/agendar")
    public String mostrarFormularioAgendar(Model model) {
        model.addAttribute("disponibilidades", disponibilidadRepository.findByDisponibleTrue());
        return "agendar-cita";
    }

    @PostMapping("/agendar")
    public String guardarCita(@RequestParam Long disponibilidadId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow();

        Disponibilidad disponibilidad = disponibilidadRepository.findById(disponibilidadId).orElseThrow();

        if (!disponibilidad.isDisponible()) {
            return "redirect:/cliente/agendar?error=ocupada";
        }

        disponibilidad.setDisponible(false);
        disponibilidadRepository.save(disponibilidad);

        Cita cita = new Cita();
        cita.setUsuario(usuario);
        cita.setServicio(disponibilidad.getServicio());
        cita.setFecha(disponibilidad.getFechaHora());
        cita.setEstado("PENDIENTE");

        citaRepository.save(cita);

        return "redirect:/?cita=ok";
    }
}
