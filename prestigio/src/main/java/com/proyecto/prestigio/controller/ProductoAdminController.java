package com.proyecto.prestigio.controller;

import com.proyecto.prestigio.model.Producto;
import com.proyecto.prestigio.repository.ProductoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile; // Para la carga de archivos (imágenes)

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption; // Para copiar archivos

/**
 * Controlador de Spring MVC para la administración de productos en la aplicación.
 * Este controlador permite a los usuarios con rol de administrador realizar operaciones CRUD
 * (Crear, Leer, Actualizar, Desactivar/Eliminar lógicamente) sobre los productos.
 * Incluye la funcionalidad para subir y gestionar imágenes de productos.
 * La ruta base para todas las operaciones de este controlador es "/admin/productos".
 * Utiliza {@code @Controller} para trabajar con vistas (plantillas HTML) y servir la interfaz de administración de productos.
 */
@Controller
@RequestMapping("/admin/productos")
public class ProductoAdminController {

    // Define la ruta donde se guardarán las imágenes.
    // En un entorno real, esta ruta debería ser configurable (ej. en application.properties).
    private static final String UPLOAD_DIR = "uploads";

    private final ProductoRepository productoRepository;

    /**
     * Constructor para inyectar la dependencia del repositorio de productos.
     * Spring se encarga de proporcionar la instancia de {@link ProductoRepository}.
     *
     * @param productoRepository Repositorio para acceder y gestionar los datos de los productos.
     */
    public ProductoAdminController(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    /**
     * Maneja las solicitudes GET a la ruta base "/admin/productos".
     * Este método recupera todos los productos activos de la base de datos y los añade al modelo
     * para ser mostrados en la vista de administración de productos. También inicializa un objeto
     * {@link Producto} vacío para el formulario de adición.
     *
     * @param model Objeto {@link Model} de Spring UI para añadir atributos que serán utilizados por la vista.
     * @return El nombre de la vista ("admin-productos.html") que muestra el listado de productos y el formulario.
     */
    @GetMapping
    public String listarProductos(Model model) {
        // Añade al modelo una lista de todos los productos que están marcados como activos.
        model.addAttribute("productos", productoRepository.findByActivoTrue());
        // Añade un nuevo objeto Producto vacío al modelo, que se usará para el formulario de "agregar nuevo producto".
        model.addAttribute("productoNuevo", new Producto());
        // Retorna el nombre de la plantilla HTML a renderizar.
        return "admin-productos";
    }

    /**
     * Maneja las solicitudes POST a la ruta "/admin/productos/agregar".
     * Este método permite agregar un nuevo producto al catálogo, incluyendo la carga de su imagen.
     * La imagen se guarda en el directorio "uploads".
     *
     * @param producto   Objeto {@link Producto} que se enlaza automáticamente desde el formulario (ModelAttribute).
     * Contiene los datos del nuevo producto (nombre, precio, etc.).
     * @param imagenFile El archivo de imagen subido por el usuario (RequestParam de tipo MultipartFile).
     * @return Una redirección a la página de administración de productos ("/admin/productos") después de agregar el producto.
     * @throws IOException Si ocurre un error durante la operación de guardado del archivo de imagen.
     */
    @PostMapping("/agregar")
    public String agregarProducto(@ModelAttribute Producto producto,
                                  @RequestParam("imagenFile") MultipartFile imagenFile) throws IOException {
        // Verifica si se ha subido un archivo de imagen.
        if (!imagenFile.isEmpty()) {
            // Genera un nombre de archivo único para evitar colisiones, usando el timestamp y el nombre original.
            String nombreArchivo = System.currentTimeMillis() + "_" + imagenFile.getOriginalFilename();
            // Define la ruta completa donde se guardará la imagen dentro del directorio 'uploads'.
            Path ruta = Paths.get(UPLOAD_DIR).resolve(nombreArchivo).toAbsolutePath();
            // Crea los directorios necesarios si no existen.
            Files.createDirectories(ruta.getParent());
            // Copia el contenido del archivo subido a la ruta de destino, reemplazando si ya existe.
            Files.copy(imagenFile.getInputStream(), ruta, StandardCopyOption.REPLACE_EXISTING);
            // Establece el nombre del archivo de la imagen en el objeto Producto.
            producto.setImagen(nombreArchivo);
        }
        producto.setActivo(true); // Asegura que el producto se cree como activo por defecto.
        productoRepository.save(producto); // Guarda el nuevo producto en la base de datos.
        return "redirect:/admin/productos"; // Redirige de vuelta a la lista de productos.
    }

    /**
     * Maneja las solicitudes GET a la ruta "/admin/productos/editar/{id}".
     * Muestra el formulario de edición precargado con los datos del producto especificado por su ID.
     * Reutiliza la misma plantilla que para listar productos y añadir uno nuevo.
     *
     * @param id    El ID del producto a editar (variable de ruta).
     * @param model Objeto {@link Model} para pasar los datos a la vista.
     * @return El nombre de la vista ("admin-productos.html") con el formulario de edición precargado.
     * @throws java.util.NoSuchElementException Si el producto con el ID proporcionado no se encuentra.
     */
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable Long id, Model model) {
        // Busca el producto por su ID. Si no lo encuentra, lanza una excepción.
        Producto producto = productoRepository.findById(id).orElseThrow();
        // Añade el producto encontrado al modelo, usando el mismo nombre de atributo que para el "nuevo producto".
        // Esto permite que el mismo formulario HTML se use para agregar y editar.
        model.addAttribute("productoNuevo", producto);
        // También añade la lista de productos activos para mantener la vista consistente.
        model.addAttribute("productos", productoRepository.findByActivoTrue());
        // Retorna la misma plantilla que para la lista y añadir productos.
        return "admin-productos";
    }

    /**
     * Maneja las solicitudes POST a la ruta "/admin/productos/editar".
     * Este método procesa la actualización de un producto existente, incluyendo la posibilidad
     * de cambiar su imagen.
     *
     * @param producto   Objeto {@link Producto} que se enlaza automáticamente desde el formulario.
     * Debe contener el ID del producto existente.
     * @param imagenFile El nuevo archivo de imagen subido (MultipartFile). Es opcional ({@code required = false}).
     * @return Una redirección a la página de administración de productos ("/admin/productos").
     * @throws IOException Si ocurre un error durante la operación de guardado del nuevo archivo de imagen.
     */
    @PostMapping("/editar")
    public String editarProducto(@ModelAttribute Producto producto,
                                 @RequestParam(value = "imagenFile", required = false) MultipartFile imagenFile) throws IOException {
        // Verifica que el producto tenga un ID y que exista en la base de datos para edición.
        if (producto.getId() == null || !productoRepository.existsById(producto.getId())) {
            // Si el producto no se encuentra, redirige con un mensaje de error.
            return "redirect:/admin/productos?error=notfound";
        }
        // Recupera el producto existente de la base de datos para mantener datos que no se editan (ej. imagen antigua si no se sube una nueva).
        Producto existente = productoRepository.findById(producto.getId()).orElse(null);

        // Si se sube una nueva imagen:
        if (imagenFile != null && !imagenFile.isEmpty()) {
            String nombreArchivo = System.currentTimeMillis() + "_" + imagenFile.getOriginalFilename();
            Path ruta = Paths.get(UPLOAD_DIR).resolve(nombreArchivo).toAbsolutePath();
            Files.createDirectories(ruta.getParent());
            Files.copy(imagenFile.getInputStream(), ruta, StandardCopyOption.REPLACE_EXISTING);
            producto.setImagen(nombreArchivo); // Actualiza el nombre de la imagen en el objeto Producto.
        } else if (existente != null) {
            // Si no se subió una nueva imagen, pero el producto existe, mantiene la imagen existente.
            producto.setImagen(existente.getImagen());
        }

        // Importante: Asegura que el estado 'activo' del producto no se pierda durante la edición
        // si no se maneja explícitamente en el formulario de edición.
        if (existente != null) {
            producto.setActivo(existente.getActivo()); // Mantén el estado 'activo' previo.
        }


        productoRepository.save(producto); // Guarda el producto actualizado en la base de datos.
        return "redirect:/admin/productos"; // Redirige de vuelta a la lista de productos.
    }

    /**
     * Maneja las solicitudes POST a la ruta "/admin/productos/eliminar".
     * Este método realiza una "eliminación lógica" de un producto, es decir,
     * en lugar de eliminarlo permanentemente de la base de datos, simplemente
     * lo marca como inactivo ({@code activo = false}). Esto permite mantener
     * un registro histórico y evitar problemas con ventas o citas pasadas.
     *
     * @param id El ID del producto a eliminar lógicamente.
     * @return Una redirección a la página de administración de productos ("/admin/productos").
     */
    @PostMapping("/eliminar")
    public String eliminarProducto(@RequestParam Long id) {
        // Busca el producto por su ID. Si se encuentra, procede a actualizar su estado.
        productoRepository.findById(id).ifPresent(p -> {
            p.setActivo(false); // Marca el producto como inactivo.
            productoRepository.save(p); // Guarda el cambio de estado en la base de datos.
        });
        return "redirect:/admin/productos"; // Redirige de vuelta a la lista de productos.
    }
}