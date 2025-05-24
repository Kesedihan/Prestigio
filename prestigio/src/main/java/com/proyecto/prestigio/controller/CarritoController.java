package com.proyecto.prestigio.controller;

import com.proyecto.prestigio.model.*;
import com.proyecto.prestigio.service.CarritoService;
import com.proyecto.prestigio.repository.ProductoRepository;
import com.proyecto.prestigio.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/carrito")
public class CarritoController {

    private final CarritoService carritoService;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    public CarritoController(CarritoService carritoService, ProductoRepository productoRepository, UsuarioRepository usuarioRepository) {
        this.carritoService = carritoService;
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public String verCarrito(Model model, Principal principal) {
        Usuario usuario = usuarioRepository.findByEmail(principal.getName()).orElseThrow();
        Carrito carrito = carritoService.obtenerOCrearCarrito(usuario);

        List<CarritoItem> items = carritoService.obtenerItems(carrito);
        double subtotal = items.stream().mapToDouble(item -> item.getProducto().getPrecio().doubleValue() * item.getCantidad()).sum();
        int totalItems = items.stream().mapToInt(CarritoItem::getCantidad).sum();
        int totalEnviosCount = 1; // O tu lógica
        double costoEnvio = 0;    // O tu lógica
        double totalCarrito = subtotal + costoEnvio;

        model.addAttribute("items", items);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("totalEnviosCount", totalEnviosCount);
        model.addAttribute("costoEnvio", costoEnvio);
        model.addAttribute("totalCarrito", totalCarrito);

        return "carrito";
    }

    @PostMapping("/actualizarCantidad")
    public String actualizarCantidad(@RequestParam Long productoId, @RequestParam int cantidad, Principal principal) {
        Usuario usuario = usuarioRepository.findByEmail(principal.getName()).orElseThrow();
        Carrito carrito = carritoService.obtenerOCrearCarrito(usuario);
        carritoService.actualizarCantidad(carrito, productoId, cantidad);
        return "redirect:/carrito";
    }

    @PostMapping("/agregar")
    public String agregarProducto(@RequestParam Long productoId, @RequestParam(defaultValue = "1") int cantidad) {
        Usuario usuario = getUsuarioActual();
        Carrito carrito = carritoService.obtenerOCrearCarrito(usuario);
        carritoService.agregarProducto(carrito, productoId, cantidad);
        return "redirect:/carrito";
    }

    @PostMapping("/eliminar")
    public String eliminarProducto(@RequestParam Long productoId) {
        Usuario usuario = getUsuarioActual();
        Carrito carrito = carritoService.obtenerOCrearCarrito(usuario);
        carritoService.eliminarProducto(carrito, productoId);
        return "redirect:/carrito";
    }

    @PostMapping("/vaciar")
    public String vaciarCarrito() {
        Usuario usuario = getUsuarioActual();
        Carrito carrito = carritoService.obtenerOCrearCarrito(usuario);
        carritoService.vaciarCarrito(carrito);
        return "redirect:/carrito";
    }

    private Usuario getUsuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return usuarioRepository.findByEmail(email).orElseThrow();
    }
}