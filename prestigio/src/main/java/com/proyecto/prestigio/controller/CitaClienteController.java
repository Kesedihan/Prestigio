package com.proyecto.prestigio.controller;

import com.proyecto.prestigio.model.Cita;
import com.proyecto.prestigio.model.Servicio;
import com.proyecto.prestigio.model.Usuario;
import com.proyecto.prestigio.repository.ServicioRepository;
import com.proyecto.prestigio.repository.UsuarioRepository;
import com.proyecto.prestigio.repository.CitaRepository; // IMPORTACIÓN RESTAURADA
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/cliente")
public class CitaClienteController {

    private final ServicioRepository servicioRepository;
    private final UsuarioRepository usuarioRepository;
    private final CitaRepository citaRepository;

    public CitaClienteController(ServicioRepository servicioRepository, UsuarioRepository usuarioRepository, CitaRepository citaRepository) {
        this.servicioRepository = servicioRepository;
        this.usuarioRepository = usuarioRepository;
        this.citaRepository = citaRepository;
    }

    @GetMapping("/agendar") // ✅ corregido
    public String mostrarFormularioAgendar(Model model) {
        List<Servicio> servicios = servicioRepository.findByEstadoTrue();
        model.addAttribute("servicios", servicios);
        return "agendar-cita";
    }

    @PostMapping("/agendar") // ✅ corregido
    public String guardarCita(@RequestParam Long servicioId,
                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fecha) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow();

        Servicio servicio = servicioRepository.findById(servicioId).orElseThrow();

        Cita cita = new Cita();
        cita.setUsuario(usuario);
        cita.setServicio(servicio);
        cita.setFecha(fecha);
        cita.setEstado("PENDIENTE");

        citaRepository.save(cita);

        return "redirect:/?cita=ok";
    }
}
