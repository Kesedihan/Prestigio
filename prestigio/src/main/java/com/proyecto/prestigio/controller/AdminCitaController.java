package com.proyecto.prestigio.controller;

import com.proyecto.prestigio.model.Cita;
import com.proyecto.prestigio.repository.CitaRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/citas")
public class AdminCitaController {

    private final CitaRepository citaRepository;

    public AdminCitaController(CitaRepository citaRepository) {
        this.citaRepository = citaRepository;
    }

    @GetMapping
    public String listarCitas(Model model) {
        model.addAttribute("citas", citaRepository.findAllByOrderByFechaAsc());
        return "admin-citas";
    }

    @PostMapping("/estado")
    public String actualizarEstado(@RequestParam Long citaId, @RequestParam String estado) {
        Cita cita = citaRepository.findById(citaId).orElseThrow();
        cita.setEstado(estado);
        citaRepository.save(cita);
        return "redirect:/admin/citas";
    }
}
