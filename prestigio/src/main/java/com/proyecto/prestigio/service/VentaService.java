package com.proyecto.prestigio.service;

import com.proyecto.prestigio.model.*;
import com.proyecto.prestigio.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VentaService {

    @Autowired
    private VentaRepository ventaRepository;
    @Autowired
    private VentaItemRepository ventaItemRepository;
    @Autowired
    private CarritoService carritoService;
    @Autowired
    private CarritoRepository carritoRepository;
    @Autowired
    private CarritoItemRepository carritoItemRepository;

    @Autowired
    private ProductoRepository productoRepository;

    public Venta registrarCompra(Usuario usuario, String direccion, String tipoPago) {
        Carrito carrito = carritoService.obtenerOCrearCarrito(usuario);
        List<CarritoItem> items = carritoService.obtenerItems(carrito);

        double total = items.stream()
                .mapToDouble(item -> item.getProducto().getPrecio().doubleValue() * item.getCantidad())
                .sum();

        Venta venta = Venta.builder()
                .usuario(usuario)
                .direccionEnvio(direccion)
                .tipoPago(tipoPago)
                .estado("EN_PROCESO")
                .fecha(LocalDateTime.now())
                .total(total)
                .build();
        venta = ventaRepository.save(venta);

        for (CarritoItem item : items) {
            Producto producto = item.getProducto();
            producto.setStock(producto.getStock() - item.getCantidad());
            productoRepository.save(producto);

            VentaItem ventaItem = VentaItem.builder()
                    .venta(venta)
                    .producto(producto)
                    .cantidad(item.getCantidad())
                    .precioUnitario(producto.getPrecio().doubleValue())
                    .build();
            ventaItemRepository.save(ventaItem);
        }

        carritoService.vaciarCarrito(carrito);
        return venta;
    }

    public List<Venta> obtenerHistorialPorUsuario(Usuario usuario) {
        return ventaRepository.findByUsuarioOrderByFechaDesc(usuario);
    }

    public List<Venta> obtenerTodas() {
        return ventaRepository.findAll();
    }

    public void cambiarEstado(Long ventaId, String nuevoEstado) {
        Venta venta = ventaRepository.findById(ventaId).orElseThrow();
        venta.setEstado(nuevoEstado);
        ventaRepository.save(venta);
    }
}
