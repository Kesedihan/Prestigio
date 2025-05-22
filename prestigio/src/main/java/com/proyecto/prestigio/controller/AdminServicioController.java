package com.proyecto.prestigio.controller;

import com.proyecto.prestigio.model.Servicio;
import com.proyecto.prestigio.repository.ServicioRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/admin/servicios")
public class AdminServicioController {

    private final ServicioRepository servicioRepository;

    public AdminServicioController(ServicioRepository servicioRepository) {
        this.servicioRepository = servicioRepository;
    }

    @GetMapping
    public String listarServicios(Model model) {
        List<Servicio> servicios = servicioRepository.findAll();
        model.addAttribute("servicios", servicios);
        return "admin-servicios"; // HTML en templates
    }

    @PostMapping
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
        return "redirect:/admin/servicios";
    }
}

