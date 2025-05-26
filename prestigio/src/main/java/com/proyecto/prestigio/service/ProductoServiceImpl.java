package com.proyecto.prestigio.service;

import com.proyecto.prestigio.model.Producto;
import com.proyecto.prestigio.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional; // Necesario para obtenerPorId

/**
 * Implementación del servicio de lógica de negocio para la gestión de productos.
 * Esta clase implementa la interfaz {@link ProductoService} y proporciona las operaciones
 * relacionadas con la entidad {@link Producto}, como listar productos activos, filtrar,
 * guardar (crear/actualizar), obtener por ID y realizar una eliminación lógica.
 * Utiliza {@code @Service} para ser reconocido como un componente de servicio de Spring.
 */
@Service
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;

    /**
     * Constructor para inyectar la dependencia del repositorio de productos.
     * Spring se encarga de proporcionar la instancia de {@link ProductoRepository}.
     * La inyección por constructor es la forma preferida en Spring para manejar dependencias,
     * ya que facilita la inmutabilidad y la testabilidad.
     *
     * @param productoRepository Repositorio para acceder y gestionar los datos de los productos.
     */
    public ProductoServiceImpl(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    /**
     * Lista todos los productos que están marcados como activos (disponibles) en la base de datos.
     *
     * @return Una {@link List} de objetos {@link Producto} activos.
     * @Override indica que este método implementa un método de la interfaz {@link ProductoService}.
     */
    @Override
    public List<Producto> listarActivos() {
        // Delega la consulta para encontrar productos activos directamente al repositorio.
        return productoRepository.findByActivoTrue();
    }

    /**
     * Filtra los productos activos basándose en un tipo y/o un rango de precios.
     * Si se proporciona un tipo, filtra por ese tipo y el rango de precios.
     * Si el tipo es nulo o vacío, solo filtra por el rango de precios.
     *
     * @param tipo El tipo de producto a filtrar (opcional).
     * @param min  El precio mínimo para el rango de filtro.
     * @param max  El precio máximo para el rango de filtro.
     * @return Una {@link List} de objetos {@link Producto} que coinciden con los criterios de filtro y están activos.
     * @Override indica que este método implementa un método de la interfaz {@link ProductoService}.
     */
    @Override
    public List<Producto> filtrar(String tipo, BigDecimal min, BigDecimal max) {
        if (tipo != null && !tipo.isEmpty()) {
            // Busca productos activos por tipo y dentro de un rango de precios.
            return productoRepository.findByActivoTrueAndTipoAndPrecioBetween(tipo, min, max);
        }
        // Si el tipo es nulo o vacío, busca solo productos activos dentro de un rango de precios.
        return productoRepository.findByActivoTrueAndPrecioBetween(min, max);
    }

    /**
     * Guarda un producto en la base de datos.
     * Este método puede ser utilizado tanto para crear un nuevo producto (si el ID es nulo)
     * como para actualizar uno existente (si el ID ya existe en la base de datos).
     *
     * @param producto El objeto {@link Producto} a guardar o actualizar.
     * @return El objeto {@link Producto} persistido, que incluirá el ID (si es nuevo)
     * o los cambios guardados (si es una actualización).
     * @Override indica que este método implementa un método de la interfaz {@link ProductoService}.
     */
    @Override
    public Producto guardar(Producto producto) {
        // Delega la operación de guardado o actualización directamente al repositorio.
        return productoRepository.save(producto);
    }

    /**
     * Obtiene un producto por su identificador único.
     *
     * @param id El ID del producto a buscar.
     * @return El objeto {@link Producto} si se encuentra, o {@code null} si no existe un producto con el ID dado.
     * @Override indica que este método implementa un método de la interfaz {@link ProductoService}.
     */
    @Override
    public Producto obtenerPorId(Long id) {
        // Delega la búsqueda al repositorio y si no se encuentra, retorna null.
        // Podría ser más robusto retornar Optional<Producto> o lanzar una excepción específica.
        return productoRepository.findById(id).orElse(null);
    }

    /**
     * Realiza una "eliminación lógica" de un producto.
     * En lugar de borrar el registro del producto de la base de datos, este método
     * simplemente actualiza el campo 'activo' a {@code false}, haciendo que el producto
     * ya no sea visible en las listas de productos activos pero manteniendo su registro histórico.
     *
     * @param id El ID del producto a eliminar lógicamente.
     * @throws RuntimeException Si el producto con el ID especificado no se encuentra.
     * @Override indica que este método implementa un método de la interfaz {@link ProductoService}.
     */
    @Override
    public void eliminar(Long id) {
        // Busca el producto por ID. Si no se encuentra, lanza una excepción.
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
        // Marca el producto como inactivo.
        producto.setActivo(false);
        // Guarda el producto actualizado con el estado inactivo.
        productoRepository.save(producto);
    }
}