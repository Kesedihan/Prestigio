package com.proyecto.prestigio.service;

import com.proyecto.prestigio.model.*;
import com.proyecto.prestigio.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio de lógica de negocio para la gestión de ventas en la aplicación.
 * Este servicio orquesta las operaciones relacionadas con la creación, consulta y actualización de ventas,
 * interactuando con los repositorios correspondientes para persistir los datos.
 * Incluye lógica para registrar compras, gestionar el historial de ventas y cambiar el estado de las ventas.
 * Utiliza {@code @Service} para ser reconocido como un componente de servicio de Spring.
 */
@Service
public class VentaService {

    // Inyección de dependencias de los repositorios necesarios para las operaciones de ventas.
    // @Autowired es una anotación que Spring usa para inyectar automáticamente los beans.
    // Aunque funciona, la inyección por constructor es generalmente preferida para mayor claridad y facilidad de testing.
    @Autowired
    private VentaRepository ventaRepository;
    @Autowired
    private VentaItemRepository ventaItemRepository;
    @Autowired
    private ProductoRepository productoRepository;

    /**
     * Registra una nueva compra en el sistema.
     * Este método es el corazón del proceso de finalización de una compra:
     * <ul>
     * <li>Calcula el total de la venta a partir de los ítems seleccionados.</li>
     * <li>Crea y guarda el objeto {@link Venta} principal.</li>
     * <li>Itera sobre los ítems seleccionados para crear y guardar cada {@link VentaItem}.</li>
     * <li>Actualiza el stock de cada {@link Producto} vendido.</li>
     * </ul>
     *
     * @param usuario         El {@link Usuario} que realiza la compra.
     * @param direccion       La dirección de envío proporcionada para esta venta.
     * @param tipoPago        El método de pago utilizado para esta venta.
     * @param itemsSeleccionados Una lista de {@link CarritoItem} que representan los productos
     * seleccionados del carrito para esta compra.
     */
    public void registrarCompraConItems(Usuario usuario, String direccion, String tipoPago, List<CarritoItem> itemsSeleccionados) {
        // Calcula el monto total de la venta sumando el precio * cantidad de cada ítem seleccionado.
        double total = itemsSeleccionados.stream()
                .mapToDouble(item -> item.getProducto().getPrecio().doubleValue() * item.getCantidad())
                .sum();

        // Construye un nuevo objeto Venta utilizando el patrón Builder (asumiendo que Venta.builder() existe).
        Venta venta = Venta.builder()
                .usuario(usuario)
                .direccionEnvio(direccion)
                .tipoPago(tipoPago)
                .estado("EN_PROCESO") // Estado inicial de una venta.
                .fecha(LocalDateTime.now()) // Fecha y hora actual de la venta.
                .total(total)
                .build();
        // Guarda la Venta en la base de datos para obtener su ID autogenerado, que es necesario para los VentaItem.
        venta = ventaRepository.save(venta);

        // Itera sobre cada CarritoItem que fue seleccionado para la compra.
        for (CarritoItem item : itemsSeleccionados) {
            Producto producto = item.getProducto();
            // Descuenta la cantidad vendida del stock del producto.
            producto.setStock(producto.getStock() - item.getCantidad());
            // Guarda el producto con el stock actualizado.
            productoRepository.save(producto);

            // Construye un nuevo objeto VentaItem para cada producto de la compra.
            VentaItem ventaItem = VentaItem.builder()
                    .venta(venta) // Asocia este ítem a la venta recién creada.
                    .producto(producto)
                    .cantidad(item.getCantidad())
                    .precioUnitario(producto.getPrecio().doubleValue()) // Guarda el precio del producto en el momento de la venta.
                    .build();
            // Guarda el VentaItem en la base de datos.
            ventaItemRepository.save(ventaItem);
        }
    }

    /**
     * Obtiene el historial de ventas (compras) de un usuario específico.
     * Las ventas se ordenan por fecha en orden descendente (las más recientes primero).
     *
     * @param usuario El {@link Usuario} del cual se desea obtener el historial de ventas.
     * @return Una {@link List} de objetos {@link Venta} asociados al usuario, ordenadas por fecha.
     */
    public List<Venta> obtenerHistorialPorUsuario(Usuario usuario) {
        // Delega la consulta al repositorio de ventas para encontrar todas las ventas de un usuario, ordenadas por fecha.
        return ventaRepository.findByUsuarioOrderByFechaDesc(usuario);
    }

    /**
     * Obtiene una lista de todas las ventas registradas en el sistema.
     * Este método es útil para la administración, donde se necesita ver un panorama completo de las ventas.
     *
     * @return Una {@link List} de todos los objetos {@link Venta} presentes en la base de datos.
     */
    public List<Venta> obtenerTodas() {
        // Delega la consulta para obtener todas las ventas al repositorio.
        return ventaRepository.findAll();
    }

    /**
     * Cambia el estado de una venta específica.
     * Este método es utilizado por la administración para actualizar el flujo de una venta (ej. de "EN_PROCESO" a "ENVIADO", "COMPLETADO", etc.).
     *
     * @param ventaId     El ID de la {@link Venta} cuyo estado se desea cambiar.
     * @param nuevoEstado La nueva cadena de estado que se asignará a la venta.
     * @throws java.util.NoSuchElementException Si la venta con el {@code ventaId} proporcionado no se encuentra.
     */
    public void cambiarEstado(Long ventaId, String nuevoEstado) {
        // Busca la venta por su ID. Si no se encuentra, lanza una excepción (orElseThrow()).
        Venta venta = ventaRepository.findById(ventaId).orElseThrow();
        // Establece el nuevo estado para la venta.
        venta.setEstado(nuevoEstado);
        // Guarda la venta actualizada en la base de datos.
        ventaRepository.save(venta);
    }
}