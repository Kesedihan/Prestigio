package com.proyecto.prestigio.controller;

import com.proyecto.prestigio.model.Producto;
import com.proyecto.prestigio.repository.ProductoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/productos")
public class ProductoAdminController {

    private final ProductoRepository productoRepository;

    public ProductoAdminController(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @GetMapping
    public String listarProductos(Model model) {
        model.addAttribute("productos", productoRepository.findAll());
        model.addAttribute("productoNuevo", new Producto()); // para el formulario
        return "admin-productos";
    }


    @PostMapping("/agregar")
    public String agregarProducto(@ModelAttribute Producto producto) {
        productoRepository.save(producto);
        return "redirect:/admin/productos";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable Long id, Model model) {
        Producto producto = productoRepository.findById(id).orElseThrow();
        model.addAttribute("productoNuevo", producto);
        model.addAttribute("productos", productoRepository.findAll());
        return "admin-productos";
    }

    @PostMapping("/editar")
    public String editarProducto(@ModelAttribute Producto producto) {
        if (!productoRepository.existsById(producto.getId())) {
            return "redirect:/admin/productos?error=notfound";
        }
        productoRepository.save(producto);
        return "redirect:/admin/productos";
    }



    @PostMapping("/eliminar")
    public String eliminarProducto(@RequestParam Long id) {
        productoRepository.deleteById(id);
        return "redirect:/admin/productos";
    }
}


