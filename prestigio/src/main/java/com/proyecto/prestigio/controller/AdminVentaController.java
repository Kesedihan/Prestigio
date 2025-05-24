package com.proyecto.prestigio.controller;

import com.proyecto.prestigio.service.VentaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/ventas")
public class AdminVentaController {

    private final VentaService ventaService;

    public AdminVentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @GetMapping
    public String verTodasLasVentas(Model model) {
        model.addAttribute("ventas", ventaService.obtenerTodas());
        return "admin-ventas";
    }



    @PostMapping("/estado")
    public String cambiarEstado(@RequestParam Long ventaId, @RequestParam String estado) {
        ventaService.cambiarEstado(ventaId, estado);
        return "redirect:/admin/ventas";
    }
}
