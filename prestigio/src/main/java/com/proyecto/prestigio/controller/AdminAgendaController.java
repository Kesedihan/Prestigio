package com.proyecto.prestigio.controller;


import com.proyecto.prestigio.model.Disponibilidad;
import com.proyecto.prestigio.model.Servicio;
import com.proyecto.prestigio.repository.DisponibilidadRepository;
import com.proyecto.prestigio.repository.ServicioRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/admin/agenda")
public class AdminAgendaController {

    private final ServicioRepository servicioRepository;
    private final DisponibilidadRepository disponibilidadRepository;

    public AdminAgendaController(ServicioRepository servicioRepository,
                                 DisponibilidadRepository disponibilidadRepository) {
        this.servicioRepository = servicioRepository;
        this.disponibilidadRepository = disponibilidadRepository;
    }

    @GetMapping
    public String mostrarAgenda(Model model) {
        model.addAttribute("servicios", servicioRepository.findAll());
        model.addAttribute("disponibilidades", disponibilidadRepository.findByDisponibleTrue());
        return "admin-agenda";
    }

    @PostMapping("/servicio")
    public String agregarServicio(@RequestParam String nombre,
                                  @RequestParam String descripcion,
                                  @RequestParam Integer duracion,
                                  @RequestParam BigDecimal precio) {
        Servicio servicio = new Servicio();
        servicio.setNombre(nombre);
        servicio.setDescripcion(descripcion);
        servicio.setDuracion(duracion);
        servicio.setPrecio(precio);
        servicio.setEstado(true);
        servicioRepository.save(servicio);
        return "redirect:/admin/agenda";
    }

    @PostMapping("/disponibilidad")
    public String agregarDisponibilidad(@RequestParam Long servicioId,
                                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaHora) {
        Servicio servicio = servicioRepository.findById(servicioId).orElseThrow();
        Disponibilidad d = new Disponibilidad(null, servicio, fechaHora, true);
        disponibilidadRepository.save(d);
        return "redirect:/admin/agenda";
    }
}
