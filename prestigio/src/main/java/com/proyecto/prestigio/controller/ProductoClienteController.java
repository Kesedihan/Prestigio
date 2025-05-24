package com.proyecto.prestigio.controller;

import com.proyecto.prestigio.model.Producto;
import com.proyecto.prestigio.repository.ProductoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.util.List;


@Controller
@RequestMapping("/productos")
public class ProductoClienteController {

    private final ProductoRepository productoRepository;

    public ProductoClienteController(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @GetMapping
    public String verProductos(@RequestParam(required = false) String tipo,
                               @RequestParam(required = false) BigDecimal minPrecio,
                               @RequestParam(required = false) BigDecimal maxPrecio,
                               Model model) {

        List<Producto> productos;

        if (tipo != null && !tipo.isEmpty()) {
            productos = productoRepository.findByActivoTrueAndTipo(tipo);
        } else if (minPrecio != null && maxPrecio != null) {
            productos = productoRepository.findByActivoTrueAndPrecioBetween(minPrecio, maxPrecio);
        } else {
            productos = productoRepository.findByActivoTrue();
        }

        model.addAttribute("productos", productos);
        return "productos";
    }
}

