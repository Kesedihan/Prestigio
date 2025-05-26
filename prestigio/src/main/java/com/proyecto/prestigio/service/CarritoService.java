package com.proyecto.prestigio.service;

import com.proyecto.prestigio.model.*;
import com.proyecto.prestigio.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio de lógica de negocio para la gestión del carrito de compras de los usuarios.
 * Este servicio encapsula todas las operaciones relacionadas con la creación, adición,
 * eliminación, actualización y vaciado de productos en el carrito de un usuario.
 * Interactúa con los repositorios de {@link Carrito}, {@link CarritoItem} y {@link Producto}.
 * Utiliza {@code @Service} para ser reconocido como un componente de servicio de Spring.
 */
@Service
public class CarritoService {

    // Inyección de dependencias de los repositorios necesarios para las operaciones del carrito.
    // Aunque @Autowired es funcional, la inyección por constructor es la práctica preferida
    // para mayor claridad, inmutabilidad y facilidad de testing.
    @Autowired
    private CarritoRepository carritoRepository;
    @Autowired
    private CarritoItemRepository carritoItemRepository;
    @Autowired
    private ProductoRepository productoRepository;

    /**
     * Obtiene el carrito de compras de un usuario específico o crea uno nuevo si no existe.
     * Este método asegura que cada usuario tenga un carrito asociado para gestionar sus compras.
     *
     * @param usuario El {@link Usuario} para el cual se desea obtener o crear el carrito.
     * @return El objeto {@link Carrito} asociado al usuario.
     */
    public Carrito obtenerOCrearCarrito(Usuario usuario) {
        // Intenta encontrar un carrito existente para el usuario.
        return carritoRepository.findByUsuario(usuario)
                // Si el carrito no existe (Optional.empty()), crea un nuevo carrito, lo guarda y lo devuelve.
                .orElseGet(() -> carritoRepository.save(Carrito.builder().usuario(usuario).build()));
    }

    /**
     * Agrega un producto al carrito de compras de un usuario.
     * Si el producto ya existe en el carrito, se incrementa su cantidad.
     * Si es un producto nuevo en el carrito, se crea un nuevo {@link CarritoItem}.
     *
     * @param carrito    El {@link Carrito} al que se desea agregar el producto.
     * @param productoId El ID del {@link Producto} a agregar.
     * @param cantidad   La cantidad del producto a agregar.
     * @throws java.util.NoSuchElementException Si el producto con el {@code productoId} no se encuentra.
     */
    public void agregarProducto(Carrito carrito, Long productoId, int cantidad) {
        // Busca el producto en la base de datos por su ID. Lanza excepción si no existe.
        Producto producto = productoRepository.findById(productoId).orElseThrow();

        // Intenta encontrar si el producto ya existe como CarritoItem en este carrito.
        Optional<CarritoItem> existente = carrito.getItems().stream()
                .filter(item -> item.getProducto().getId().equals(productoId))
                .findFirst();

        if (existente.isPresent()) {
            // Si el producto ya está en el carrito, actualiza la cantidad del CarritoItem existente.
            CarritoItem item = existente.get();
            item.setCantidad(item.getCantidad() + cantidad);
            carritoItemRepository.save(item); // Persiste la actualización del item.
        } else {
            // Si el producto no está en el carrito, crea un nuevo CarritoItem.
            CarritoItem item = CarritoItem.builder()
                    .carrito(carrito)
                    .producto(producto)
                    .cantidad(cantidad)
                    .build();
            carritoItemRepository.save(item); // Persiste el nuevo item.
            // Es IMPORTANTE añadir el item a la lista en memoria del carrito
            // para que futuras operaciones dentro de la misma transacción vean el cambio.
            carrito.getItems().add(item);
            // Aunque los items se guardan individualmente, guardar el carrito asegura que la relación
            // y cualquier cambio en el objeto Carrito principal se persista.
            carritoRepository.save(carrito);
        }
    }

    /**
     * Elimina múltiples ítems de un carrito de compras basándose en una lista de IDs de productos.
     * Esto es útil cuando el usuario selecciona varios ítems para eliminar de una vez.
     *
     * @param carrito             El {@link Carrito} del cual se eliminarán los ítems.
     * @param productosSeleccionados Una {@link List} de IDs de productos que se desean eliminar del carrito.
     */
    public void eliminarItems(Carrito carrito, List<Long> productosSeleccionados) {
        // Filtra los CarritoItem que corresponden a los productos seleccionados para eliminar.
        List<CarritoItem> itemsAEliminar = carrito.getItems().stream()
                .filter(item -> productosSeleccionados.contains(item.getProducto().getId()))
                .collect(Collectors.toList());

        // Remueve los ítems de la lista en memoria del carrito.
        carrito.getItems().removeAll(itemsAEliminar);
        // Elimina los ítems correspondientes de la base de datos.
        carritoItemRepository.deleteAll(itemsAEliminar);
        // Guarda el carrito para reflejar los cambios en la relación (si es una relación fuerte).
        carritoRepository.save(carrito);
    }

    /**
     * Elimina un producto específico (y su {@link CarritoItem} asociado) del carrito.
     *
     * @param carrito    El {@link Carrito} del cual se eliminará el producto.
     * @param productoId El ID del {@link Producto} a eliminar del carrito.
     */
    public void eliminarProducto(Carrito carrito, Long productoId) {
        // Encuentra y remueve el CarritoItem correspondiente al productoId de la lista en memoria.
        // removeIf es un método conveniente para eliminar elementos basados en una condición.
        carrito.getItems().removeIf(item -> item.getProducto().getId().equals(productoId));
        // Guarda el carrito. Esto puede disparar la eliminación del CarritoItem en la base de datos
        // si la relación entre Carrito y CarritoItem tiene configurado CascadeType.ALL o orphanRemoval=true.
        // De lo contrario, se necesitaría una eliminación explícita a través de carritoItemRepository.
        carritoRepository.save(carrito);
    }

    /**
     * Actualiza la cantidad de un producto específico en el carrito.
     *
     * @param carrito    El {@link Carrito} que contiene el producto.
     * @param productoId El ID del {@link Producto} cuya cantidad se desea actualizar.
     * @param cantidad   La nueva cantidad para el producto.
     */
    public void actualizarCantidad(Carrito carrito, Long productoId, int cantidad) {
        // Busca el CarritoItem que corresponde al productoId.
        carrito.getItems().stream()
                .filter(item -> item.getProducto().getId().equals(productoId))
                .findFirst()
                .ifPresent(item -> {
                    // Si se encuentra el item, actualiza su cantidad y lo guarda en la base de datos.
                    item.setCantidad(cantidad);
                    carritoItemRepository.save(item);
                });
    }

    /**
     * Vacía completamente el carrito de compras, eliminando todos sus {@link CarritoItem}.
     *
     * @param carrito El {@link Carrito} a vaciar.
     */
    public void vaciarCarrito(Carrito carrito) {
        // Elimina todos los CarritoItem asociados a este carrito de la base de datos.
        carritoItemRepository.deleteAll(carrito.getItems());
        // Limpia la lista de ítems en memoria del carrito.
        carrito.getItems().clear();
        // Guarda el carrito para reflejar el vaciado (aunque puede no ser estrictamente necesario
        // si deleteAll ya maneja las relaciones).
        carritoRepository.save(carrito);
    }

    /**
     * Obtiene la lista de {@link CarritoItem}s asociados a un carrito específico.
     *
     * @param carrito El {@link Carrito} del cual se desean obtener los ítems.
     * @return Una {@link List} de objetos {@link CarritoItem} en el carrito.
     */
    public List<CarritoItem> obtenerItems(Carrito carrito) {
        // Delega la búsqueda de ítems por carrito al repositorio de CarritoItem.
        return carritoItemRepository.findByCarrito(carrito);
    }
}