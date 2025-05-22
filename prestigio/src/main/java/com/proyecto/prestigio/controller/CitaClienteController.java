package com.proyecto.prestigio.controller;

import com.proyecto.prestigio.model.Cita;
import com.proyecto.prestigio.model.Disponibilidad;
import com.proyecto.prestigio.model.Servicio;
import com.proyecto.prestigio.model.Usuario;
import com.proyecto.prestigio.repository.CitaRepository;
import com.proyecto.prestigio.repository.DisponibilidadRepository;
import com.proyecto.prestigio.repository.ServicioRepository;
import com.proyecto.prestigio.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/cliente")
public class CitaClienteController {

    private final DisponibilidadRepository disponibilidadRepository;
    private final UsuarioRepository usuarioRepository;
    private final CitaRepository citaRepository;
    private final ServicioRepository servicioRepository;

    public CitaClienteController(DisponibilidadRepository disponibilidadRepository,
                                 UsuarioRepository usuarioRepository,
                                 CitaRepository citaRepository,
                                 ServicioRepository servicioRepository) {
        this.disponibilidadRepository = disponibilidadRepository;
        this.usuarioRepository = usuarioRepository;
        this.citaRepository = citaRepository;
        this.servicioRepository = servicioRepository;
    }

    // GET: mostrar formulario de agendado
    @GetMapping("/agendar")
    public String mostrarFormularioAgendar(@RequestParam(required = false) Long servicioId, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);

        List<Servicio> servicios = servicioRepository.findByEstadoTrue();
        List<Disponibilidad> disponibilidades = (servicioId != null)
                ? disponibilidadRepository.findByDisponibleTrueAndServicioId(servicioId)
                : disponibilidadRepository.findByDisponibleTrue();

        List<Cita> citasPendientes = citaRepository.findByUsuarioAndEstado(usuario, "PENDIENTE");

        model.addAttribute("servicios", servicios);
        model.addAttribute("disponibilidades", disponibilidades);
        model.addAttribute("citasPendientes", citasPendientes);
        model.addAttribute("servicioSeleccionado", servicioId);

        return "agendar-cita";
    }



    // POST: guardar cita
    @PostMapping("/agendar")
    public String guardarCita(@RequestParam Long disponibilidadId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow();

        Disponibilidad disponibilidad = disponibilidadRepository.findById(disponibilidadId).orElseThrow();

        if (!disponibilidad.isDisponible()) {
            return "redirect:/cliente/agendar?error=ocupada";
        }

        // Marcar como no disponible
        disponibilidad.setDisponible(false);
        disponibilidadRepository.save(disponibilidad);

        // Crear cita
        Cita cita = new Cita();
        cita.setUsuario(usuario);
        cita.setServicio(disponibilidad.getServicio());
        cita.setFecha(disponibilidad.getFechaHora());
        cita.setEstado("PENDIENTE");

        citaRepository.save(cita);

        return "redirect:/mi-cuenta?agendada=ok";
    }
}
