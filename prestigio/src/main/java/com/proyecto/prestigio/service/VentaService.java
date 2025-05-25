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
    private ProductoRepository productoRepository;

    public void registrarCompraConItems(Usuario usuario, String direccion, String tipoPago, List<CarritoItem> itemsSeleccionados) {
        double total = itemsSeleccionados.stream()
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

        for (CarritoItem item : itemsSeleccionados) {
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
