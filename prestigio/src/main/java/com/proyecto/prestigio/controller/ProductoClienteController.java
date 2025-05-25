package com.proyecto.prestigio.controller;

import com.proyecto.prestigio.model.Producto;
import com.proyecto.prestigio.repository.ProductoRepository;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    // Nuevo endpoint para servir imágenes desde /uploads
    @GetMapping("/uploads/{nombreImagen:.+}")
    @ResponseBody
    public ResponseEntity<Resource> verImagen(@PathVariable String nombreImagen) throws MalformedURLException {
        Path ruta = Paths.get("uploads").resolve(nombreImagen).toAbsolutePath();
        Resource recurso = new UrlResource(ruta.toUri());
        if (recurso.exists() && recurso.isReadable()) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + recurso.getFilename() + "\"")
                    .body(recurso);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

