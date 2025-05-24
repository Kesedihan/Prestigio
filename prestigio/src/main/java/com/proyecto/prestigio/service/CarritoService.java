package com.proyecto.prestigio.service;

import com.proyecto.prestigio.model.*;
import com.proyecto.prestigio.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CarritoService {

    @Autowired
    private CarritoRepository carritoRepository;
    @Autowired
    private CarritoItemRepository carritoItemRepository;
    @Autowired
    private ProductoRepository productoRepository;

    public Carrito obtenerOCrearCarrito(Usuario usuario) {
        return carritoRepository.findByUsuario(usuario)
                .orElseGet(() -> carritoRepository.save(Carrito.builder().usuario(usuario).build()));
    }

    public void agregarProducto(Carrito carrito, Long productoId, int cantidad) {
        Producto producto = productoRepository.findById(productoId).orElseThrow();
        Optional<CarritoItem> existente = carrito.getItems().stream()
                .filter(item -> item.getProducto().getId().equals(productoId))
                .findFirst();
        if (existente.isPresent()) {
            CarritoItem item = existente.get();
            item.setCantidad(item.getCantidad() + cantidad);
            carritoItemRepository.save(item);
        } else {
            CarritoItem item = CarritoItem.builder()
                    .carrito(carrito)
                    .producto(producto)
                    .cantidad(cantidad)
                    .build();
            carritoItemRepository.save(item);
            carrito.getItems().add(item); // <-- IMPORTANTE: actualiza la lista en memoria
            carritoRepository.save(carrito);
        }
    }

    public void eliminarProducto(Carrito carrito, Long productoId) {
        carrito.getItems().removeIf(item -> item.getProducto().getId().equals(productoId));
        carritoRepository.save(carrito);
    }
    public void actualizarCantidad(Carrito carrito, Long productoId, int cantidad) {
        carrito.getItems().stream()
                .filter(item -> item.getProducto().getId().equals(productoId))
                .findFirst()
                .ifPresent(item -> {
                    item.setCantidad(cantidad);
                    carritoItemRepository.save(item);
                });
    }

    public void vaciarCarrito(Carrito carrito) {
        carritoItemRepository.deleteAll(carrito.getItems());
        carrito.getItems().clear();
        carritoRepository.save(carrito);
    }

    public List<CarritoItem> obtenerItems(Carrito carrito) {
        return carritoItemRepository.findByCarrito(carrito);
    }
}
