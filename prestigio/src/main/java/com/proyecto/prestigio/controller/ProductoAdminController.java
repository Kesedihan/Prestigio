package com.proyecto.prestigio.controller;

import com.proyecto.prestigio.model.Producto;
import com.proyecto.prestigio.repository.ProductoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

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
    public String agregarProducto(@ModelAttribute Producto producto,
                                  @RequestParam("imagenFile") MultipartFile imagenFile) throws IOException {
        if (!imagenFile.isEmpty()) {
            String nombreArchivo = System.currentTimeMillis() + "_" + imagenFile.getOriginalFilename();
            Path ruta = Paths.get("uploads").resolve(nombreArchivo).toAbsolutePath();
            Files.createDirectories(ruta.getParent());
            Files.copy(imagenFile.getInputStream(), ruta, StandardCopyOption.REPLACE_EXISTING);
            producto.setImagen(nombreArchivo);
        }
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
    public String editarProducto(@ModelAttribute Producto producto,
                                 @RequestParam(value = "imagenFile", required = false) MultipartFile imagenFile) throws IOException {
        if (producto.getId() == null || !productoRepository.existsById(producto.getId())) {
            return "redirect:/admin/productos?error=notfound";
        }
        Producto existente = productoRepository.findById(producto.getId()).orElse(null);
        if (imagenFile != null && !imagenFile.isEmpty()) {
            String nombreArchivo = System.currentTimeMillis() + "_" + imagenFile.getOriginalFilename();
            Path ruta = Paths.get("uploads").resolve(nombreArchivo).toAbsolutePath();
            Files.createDirectories(ruta.getParent());
            Files.copy(imagenFile.getInputStream(), ruta, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            producto.setImagen(nombreArchivo);
        } else if (existente != null) {
            producto.setImagen(existente.getImagen());
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