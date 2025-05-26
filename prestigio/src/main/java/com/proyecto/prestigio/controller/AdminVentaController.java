package com.proyecto.prestigio.controller;

import com.proyecto.prestigio.model.Venta;
import com.proyecto.prestigio.service.VentaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador de Spring MVC para la administración de ventas en la aplicación.
 * Permite a los usuarios con rol de administrador visualizar todas las ventas,
 * aplicar filtros para buscar ventas específicas (por fecha, producto o cliente),
 * y actualizar el estado de una venta.
 * La ruta base para todas las operaciones de este controlador es "/admin/ventas".
 * Utiliza {@code @Controller} para trabajar con vistas (plantillas HTML) y servir la interfaz de administración de ventas.
 */
@Controller
@RequestMapping("/admin/ventas")
public class AdminVentaController {

    private final VentaService ventaService;

    /**
     * Constructor para inyectar la dependencia del servicio de ventas.
     * Spring se encarga de proporcionar la instancia de {@link VentaService}.
     *
     * @param ventaService El servicio que gestiona la lógica de negocio relacionada con las ventas.
     */
    public AdminVentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    /**
     * Maneja las solicitudes GET a la ruta base "/admin/ventas".
     * Este método recupera todas las ventas y permite filtrarlas opcionalmente
     * por fecha, nombre de producto o nombre del cliente.
     * Los resultados filtrados son añadidos al modelo para ser mostrados en la vista.
     *
     * @param fecha    Parámetro opcional para filtrar las ventas por una fecha específica (formato "YYYY-MM-DD").
     * @param producto Parámetro opcional para filtrar las ventas por el nombre de un producto contenido en los ítems de venta.
     * @param cliente  Parámetro opcional para filtrar las ventas por el nombre de un cliente asociado a la venta.
     * @param model    Objeto {@link Model} de Spring UI para añadir atributos que serán utilizados por la vista.
     * @return El nombre de la vista ("admin-ventas.html") que muestra el listado de ventas.
     */
    @GetMapping
    public String verTodasLasVentas(
            @RequestParam(required = false) String fecha,
            @RequestParam(required = false) String producto,
            @RequestParam(required = false) String cliente,
            Model model) {

        // Obtiene la lista completa de todas las ventas del servicio.
        List<Venta> ventas = ventaService.obtenerTodas();

        // Si se proporciona una fecha, filtra la lista de ventas.
        if (fecha != null && !fecha.isEmpty()) {
            ventas = ventas.stream()
                    // Filtra las ventas cuya fecha (en formato de cadena LocalDate) coincide con la fecha proporcionada.
                    .filter(v -> v.getFecha() != null && v.getFecha().toLocalDate().toString().equals(fecha))
                    .collect(Collectors.toList());
        }

        // Si se proporciona un nombre de producto, filtra la lista de ventas.
        if (producto != null && !producto.isEmpty()) {
            ventas = ventas.stream()
                    // Verifica si algún ítem de la venta contiene el nombre del producto (ignorando mayúsculas/minúsculas).
                    .filter(v -> v.getItems().stream()
                            .anyMatch(item -> item.getProducto() != null &&
                                    item.getProducto().getNombre().toLowerCase().contains(producto.toLowerCase())))
                    .collect(Collectors.toList());
        }

        // Si se proporciona un nombre de cliente, filtra la lista de ventas.
        if (cliente != null && !cliente.isEmpty()) {
            ventas = ventas.stream()
                    // Verifica si el nombre del usuario (cliente) asociado a la venta contiene la cadena del cliente.
                    .filter(v -> v.getUsuario() != null &&
                            v.getUsuario().getNombre().toLowerCase().contains(cliente.toLowerCase()))
                    .collect(Collectors.toList());
        }

        // Añade la lista de ventas (filtrada o completa) al modelo para ser renderizada en la vista.
        model.addAttribute("ventas", ventas);
        // Retorna el nombre de la plantilla HTML.
        return "admin-ventas";
    }


    /**
     * Maneja las solicitudes POST a la ruta "/admin/ventas/estado" para cambiar el estado de una venta.
     * Este método recibe el ID de la venta y el nuevo estado deseado, y delega la lógica de actualización
     * al {@link VentaService}.
     *
     * @param ventaId El ID de la venta cuyo estado se desea actualizar.
     * @param estado  La nueva cadena de estado para la venta (ej. "PENDIENTE", "PAGADA", "CANCELADA").
     * @return Una redirección a la página de listado de ventas ("/admin/ventas") después de la actualización.
     */
    @PostMapping("/estado")
    public String cambiarEstado(@RequestParam Long ventaId, @RequestParam String estado) {
        // Delega la lógica de negocio para cambiar el estado de la venta al servicio.
        ventaService.cambiarEstado(ventaId, estado);
        // Redirige al administrador a la página de listado de ventas para ver los cambios.
        return "redirect:/admin/ventas";
    }
}