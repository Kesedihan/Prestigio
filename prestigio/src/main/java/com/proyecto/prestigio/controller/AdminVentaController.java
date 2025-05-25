package com.proyecto.prestigio.controller;

import com.proyecto.prestigio.model.Venta;
import com.proyecto.prestigio.service.VentaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/ventas")
public class AdminVentaController {

    private final VentaService ventaService;

    public AdminVentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @GetMapping
    public String verTodasLasVentas(
            @RequestParam(required = false) String fecha,
            @RequestParam(required = false) String producto,
            @RequestParam(required = false) String cliente,
            Model model) {

        List<Venta> ventas = ventaService.obtenerTodas();

        // Filtrar por fecha (si se envió)
        if (fecha != null && !fecha.isEmpty()) {
            ventas = ventas.stream()
                    .filter(v -> v.getFecha() != null && v.getFecha().toLocalDate().toString().equals(fecha))
                    .collect(Collectors.toList());
        }

        // Filtrar por producto (si se envió)
        if (producto != null && !producto.isEmpty()) {
            ventas = ventas.stream()
                    .filter(v -> v.getItems().stream()
                            .anyMatch(item -> item.getProducto() != null &&
                                    item.getProducto().getNombre().toLowerCase().contains(producto.toLowerCase())))
                    .collect(Collectors.toList());
        }

        // Filtrar por cliente (si se envió)
        if (cliente != null && !cliente.isEmpty()) {
            ventas = ventas.stream()
                    .filter(v -> v.getUsuario() != null &&
                            v.getUsuario().getNombre().toLowerCase().contains(cliente.toLowerCase()))
                    .collect(Collectors.toList());
        }

        model.addAttribute("ventas", ventas);
        return "admin-ventas";
    }



    @PostMapping("/estado")
    public String cambiarEstado(@RequestParam Long ventaId, @RequestParam String estado) {
        ventaService.cambiarEstado(ventaId, estado);
        return "redirect:/admin/ventas";
    }
}
