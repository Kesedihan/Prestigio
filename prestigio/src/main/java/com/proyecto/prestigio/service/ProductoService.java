package com.proyecto.prestigio.service;

import com.proyecto.prestigio.model.Producto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Interfaz de servicio para la gestión de productos en la aplicación.
 * Define el contrato para las operaciones de negocio relacionadas con la entidad {@link Producto}.
 * Cualquier clase que implemente esta interfaz debe proporcionar una implementación para los métodos declarados aquí.
 * Esto promueve una arquitectura modular, la separación de responsabilidades y facilita el cambio de implementaciones de servicio.
 */
public interface ProductoService {

    /**
     * Define la operación para obtener una lista de todos los productos que están activos (disponibles).
     *
     * @return Una {@link List} de objetos {@link Producto} que tienen el estado 'activo' en {@code true}.
     */
    List<Producto> listarActivos();

    /**
     * Define la operación para filtrar productos activos basándose en un tipo y/o un rango de precios.
     *
     * @param tipo El tipo de producto por el cual filtrar (puede ser {@code null} o vacío si no se aplica este filtro).
     * @param min  El precio mínimo para el rango de filtro.
     * @param max  El precio máximo para el rango de filtro.
     * @return Una {@link List} de objetos {@link Producto} que cumplen con los criterios de filtro y están activos.
     */
    List<Producto> filtrar(String tipo, BigDecimal min, BigDecimal max);

    /**
     * Define la operación para guardar un producto.
     * Este método puede ser utilizado para crear un nuevo producto o para actualizar uno existente.
     *
     * @param producto El objeto {@link Producto} a ser guardado o actualizado.
     * @return El objeto {@link Producto} persistido, que reflejará el estado después de la operación (incluyendo el ID si es nuevo).
     */
    Producto guardar(Producto producto);

    /**
     * Define la operación para obtener un producto por su identificador único.
     *
     * @param id El ID del producto a buscar.
     * @return El objeto {@link Producto} si se encuentra, o {@code null} si no existe un producto con el ID dado.
     * (Nota: En implementaciones más modernas, a menudo se prefiere {@link java.util.Optional} para un manejo de ausencia de valor más explícito).
     */
    Producto obtenerPorId(Long id);

    /**
     * Define la operación para realizar una eliminación lógica de un producto.
     * En lugar de eliminar el registro de la base de datos, este método cambiará el estado
     * del producto a inactivo (por ejemplo, estableciendo una bandera 'activo' a {@code false}).
     *
     * @param id El ID del producto a eliminar lógicamente.
     */
    void eliminar(Long id);
}