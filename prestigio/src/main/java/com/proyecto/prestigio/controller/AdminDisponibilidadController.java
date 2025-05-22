package com.proyecto.prestigio.controller;

import com.proyecto.prestigio.model.Disponibilidad;
import com.proyecto.prestigio.model.Servicio;
import com.proyecto.prestigio.repository.DisponibilidadRepository;
import com.proyecto.prestigio.repository.ServicioRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/admin/disponibilidad")
public class AdminDisponibilidadController {

    private final DisponibilidadRepository disponibilidadRepository;
    private final ServicioRepository servicioRepository;

    public AdminDisponibilidadController(DisponibilidadRepository disponibilidadRepository,
                                         ServicioRepository servicioRepository) {
        this.disponibilidadRepository = disponibilidadRepository;
        this.servicioRepository = servicioRepository;
    }

    @GetMapping
    public String mostrarFormulario(Model model) {
        model.addAttribute("servicios", servicioRepository.findAll());
        model.addAttribute("disponibilidades", disponibilidadRepository.findByDisponibleTrue());
        return "admin-disponibilidad";
    }

    @PostMapping
    public String agregarDisponibilidad(@RequestParam Long servicioId,
                                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaHora) {

        Servicio servicio = servicioRepository.findById(servicioId).orElseThrow();

        Disponibilidad disponibilidad = new Disponibilidad();
        disponibilidad.setServicio(servicio);
        disponibilidad.setFechaHora(fechaHora);
        disponibilidad.setDisponible(true);

        disponibilidadRepository.save(disponibilidad);

        return "redirect:/admin/disponibilidad";
    }
}

