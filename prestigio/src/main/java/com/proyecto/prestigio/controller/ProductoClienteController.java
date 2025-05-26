package com.proyecto.prestigio.controller;

import com.proyecto.prestigio.model.Producto;
import com.proyecto.prestigio.repository.ProductoRepository;
import org.springframework.core.io.Resource;      // Para manejar recursos (archivos)
import org.springframework.core.io.UrlResource;  // Para recursos basados en URL
import org.springframework.http.HttpHeaders;     // Para cabeceras HTTP
import org.springframework.http.ResponseEntity;  // Para construir respuestas HTTP
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.net.MalformedURLException; // Para manejar excepciones de URL mal formadas
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Controlador de Spring MVC para la visualización de productos por parte de los clientes.
 * Este controlador se encarga de:
 * <ul>
 * <li>Listar productos disponibles, con opciones de filtrado por tipo o rango de precio.</li>
 * <li>Servir las imágenes de los productos almacenadas en el sistema de archivos.</li>
 * </ul>
 * La ruta base para todas las operaciones relacionadas con la visualización de productos para clientes es "/productos".
 * Utiliza {@code @Controller} para trabajar con vistas (plantillas HTML) para la interfaz de usuario.
 */
@Controller
@RequestMapping("/productos")
public class ProductoClienteController {

    private final ProductoRepository productoRepository;

    /**
     * Constructor para inyectar la dependencia del repositorio de productos.
     * Spring se encarga de proporcionar la instancia de {@link ProductoRepository}.
     *
     * @param productoRepository Repositorio para acceder y gestionar los datos de los productos.
     */
    public ProductoClienteController(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    /**
     * Maneja las solicitudes GET a la ruta base "/productos".
     * Este método recupera una lista de productos activos, aplicando filtros opcionales
     * por tipo de producto o por un rango de precios. Si no se especifican filtros,
     * se muestran todos los productos activos.
     *
     * @param tipo      Parámetro opcional para filtrar productos por su tipo (ej. "Cabello", "Piel").
     * @param minPrecio Parámetro opcional para el precio mínimo en un filtro por rango de precios.
     * @param maxPrecio Parámetro opcional para el precio máximo en un filtro por rango de precios.
     * @param model     Objeto {@link Model} de Spring UI para añadir atributos que serán utilizados por la vista.
     * @return El nombre de la vista ("productos.html") que muestra el catálogo de productos.
     */
    @GetMapping
    public String verProductos(@RequestParam(required = false) String tipo,
                               @RequestParam(required = false) BigDecimal minPrecio,
                               @RequestParam(required = false) BigDecimal maxPrecio,
                               Model model) {

        List<Producto> productos;

        // Lógica de filtrado de productos.
        if (tipo != null && !tipo.isEmpty()) {
            // Filtra por tipo de producto y asegura que sean activos.
            productos = productoRepository.findByActivoTrueAndTipo(tipo);
        } else if (minPrecio != null && maxPrecio != null) {
            // Filtra por rango de precio y asegura que sean activos.
            productos = productoRepository.findByActivoTrueAndPrecioBetween(minPrecio, maxPrecio);
        } else {
            // Si no hay filtros, obtiene todos los productos activos.
            productos = productoRepository.findByActivoTrue();
        }

        // Añade la lista de productos (filtrada o completa) al modelo para ser renderizada en la vista.
        model.addAttribute("productos", productos);
        // Retorna el nombre de la plantilla HTML.
        return "productos";
    }

    /**
     * Nuevo endpoint para servir archivos de imagen estáticos desde el directorio 'uploads'.
     * Maneja las solicitudes GET a la ruta "/productos/uploads/{nombreImagen}".
     * Permite que las imágenes de los productos sean accesibles directamente por la URL en el navegador.
     *
     * @param nombreImagen El nombre del archivo de imagen a servir (ej. "16788812345_shampoo.jpg").
     * @return Un {@link ResponseEntity} que contiene el recurso de la imagen y las cabeceras HTTP adecuadas.
     * Retorna un estado HTTP 404 (Not Found) si la imagen no existe o no se puede leer.
     * @throws MalformedURLException Si la ruta del archivo es una URL mal formada.
     */
    @GetMapping("/uploads/{nombreImagen:.+}") // ".+" permite que el nombre de archivo contenga puntos
    @ResponseBody // Indica que la respuesta es el cuerpo de la respuesta HTTP, no un nombre de vista.
    public ResponseEntity<Resource> verImagen(@PathVariable String nombreImagen) throws MalformedURLException {
        // Construye la ruta completa del archivo de imagen dentro del directorio "uploads".
        Path ruta = Paths.get("uploads").resolve(nombreImagen).toAbsolutePath();
        // Crea un recurso URL a partir de la URI de la ruta.
        Resource recurso = new UrlResource(ruta.toUri());

        // Verifica si el recurso (imagen) existe y si es legible.
        if (recurso.exists() && recurso.isReadable()) {
            // Si la imagen existe y es legible, construye la respuesta HTTP.
            return ResponseEntity.ok()
                    // Establece la cabecera Content-Disposition a "inline" para que el navegador intente mostrarla
                    // en lugar de descargarla, y especifica el nombre del archivo.
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + recurso.getFilename() + "\"")
                    // Adjunta el recurso de la imagen al cuerpo de la respuesta HTTP.
                    .body(recurso);
        } else {
            // Si la imagen no existe o no es legible, retorna un estado HTTP 404 (Not Found).
            return ResponseEntity.notFound().build();
        }
    }
}