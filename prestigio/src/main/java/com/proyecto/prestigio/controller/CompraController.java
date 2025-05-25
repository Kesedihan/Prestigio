package com.proyecto.prestigio.controller;

import com.proyecto.prestigio.model.Carrito;
import com.proyecto.prestigio.model.CarritoItem;
import com.proyecto.prestigio.model.Usuario;
import com.proyecto.prestigio.service.CarritoService;
import com.proyecto.prestigio.service.VentaService;
import com.proyecto.prestigio.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/comprar")
public class CompraController {

    private final VentaService ventaService;
    private final UsuarioRepository usuarioRepository;
    private final CarritoService carritoService;

    public CompraController(VentaService ventaService, UsuarioRepository usuarioRepository, CarritoService carritoService) {
        this.ventaService = ventaService;
        this.usuarioRepository = usuarioRepository;
        this.carritoService = carritoService;
    }

    @PostMapping
    public String comprar(@RequestParam("direccion") String direccion,
                          @RequestParam("tipoPago") String tipoPago,
                          @RequestParam("selectedProducts") String selectedProducts,
                          Principal principal,
                          RedirectAttributes redirectAttributes) {

        Usuario usuario = usuarioRepository.findByEmail(principal.getName()).orElseThrow();
        Carrito carrito = carritoService.obtenerOCrearCarrito(usuario);

        List<Long> productosSeleccionados = Arrays.stream(selectedProducts.split(","))
                .filter(s -> !s.isEmpty())
                .map(Long::parseLong)
                .collect(Collectors.toList());

        List<CarritoItem> itemsSeleccionados = carrito.getItems().stream()
                .filter(item -> productosSeleccionados.contains(item.getProducto().getId()))
                .collect(Collectors.toList());

        // Validar stock
        for (CarritoItem item : itemsSeleccionados) {
            int stockDisponible = item.getProducto().getStock();
            if (item.getCantidad() > stockDisponible) {
                redirectAttributes.addFlashAttribute("errorStock",
                        "No hay suficiente stock para el producto: " + item.getProducto().getNombre());
                return "redirect:/carrito";
            }
        }

        // Procesar la compra solo con los productos seleccionados
        ventaService.registrarCompraConItems(usuario, direccion, tipoPago, itemsSeleccionados);

        // Eliminar solo los items comprados del carrito
        carritoService.eliminarItems(carrito, productosSeleccionados);

        return "redirect:/mi-cuenta?compraExitosa";
    }

    private Usuario getUsuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return usuarioRepository.findByEmail(email).orElseThrow();
    }
}