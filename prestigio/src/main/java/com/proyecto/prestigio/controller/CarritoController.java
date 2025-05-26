package com.proyecto.prestigio.controller;

import com.proyecto.prestigio.model.*; // Asegúrate de que todas las clases model estén aquí
import com.proyecto.prestigio.service.CarritoService; // Importa el servicio de carrito
import com.proyecto.prestigio.repository.ProductoRepository;
import com.proyecto.prestigio.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal; // Importa Principal para obtener el usuario autenticado
import java.util.List;
import java.util.stream.Collectors; // Asegúrate de que Collectors esté importado para el stream

import java.math.BigDecimal; // Importa BigDecimal para el precio

/**
 * Controlador de Spring MVC para gestionar las operaciones del carrito de compras de un usuario.
 * Este controlador maneja las solicitudes relacionadas con la visualización del carrito,
 * la adición, actualización de cantidad, eliminación y vaciado de productos en el carrito.
 * La ruta base para todas las operaciones de este controlador es "/carrito".
 * Utiliza {@code @Controller} para trabajar con vistas (plantillas HTML) y servir la interfaz del carrito.
 */
@Controller
@RequestMapping("/carrito")
public class CarritoController {

    private final CarritoService carritoService;
    private final ProductoRepository productoRepository; // No se usa directamente en los métodos actuales del controlador, pero es una dependencia inyectada.
    private final UsuarioRepository usuarioRepository;

    /**
     * Constructor para inyectar las dependencias necesarias.
     * Spring se encarga de proporcionar las instancias (beans) de los servicios y repositorios.
     *
     * @param carritoService   El servicio que gestiona la lógica de negocio del carrito de compras.
     * @param productoRepository Repositorio para acceder a los datos de los productos.
     * Aunque no se usa directamente en los métodos GET/POST de este controlador,
     * podría ser utilizado por {@link CarritoService}.
     * @param usuarioRepository  Repositorio para acceder a los datos de los usuarios.
     * Es crucial para obtener el usuario autenticado y su carrito.
     */
    public CarritoController(CarritoService carritoService, ProductoRepository productoRepository, UsuarioRepository usuarioRepository) {
        this.carritoService = carritoService;
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Maneja las solicitudes GET a la ruta base "/carrito".
     * Muestra la página del carrito de compras del usuario autenticado,
     * incluyendo los ítems, subtotales y cálculos de envío.
     *
     * @param model   Objeto {@link Model} de Spring UI para añadir atributos que serán utilizados por la vista.
     * @param principal Objeto {@link Principal} que representa al usuario autenticado,
     * útil para obtener su nombre de usuario (email).
     * @return El nombre de la vista ("carrito.html") que muestra el contenido del carrito.
     * @throws java.util.NoSuchElementException Si el usuario autenticado no se encuentra en la base de datos,
     * lo cual indica un problema de configuración o datos.
     */
    @GetMapping
    public String verCarrito(Model model, Principal principal) {
        // Obtiene el usuario autenticado utilizando el email del Principal.
        Usuario usuario = usuarioRepository.findByEmail(principal.getName()).orElseThrow();
        // Obtiene o crea el carrito para el usuario.
        Carrito carrito = carritoService.obtenerOCrearCarrito(usuario);

        // Obtiene todos los ítems del carrito.
        List<CarritoItem> items = carritoService.obtenerItems(carrito);

        // Calcula el subtotal de los ítems en el carrito.
        double subtotal = items.stream()
                .mapToDouble(item -> item.getProducto().getPrecio().doubleValue() * item.getCantidad())
                .sum();
        // Calcula el número total de ítems individuales en el carrito.
        int totalItems = items.stream().mapToInt(CarritoItem::getCantidad).sum();

        // Lógica de cálculo de envío (aquí simplificada).
        int totalEnviosCount = 1; // Podría ser basada en el número de productos, vendedores, etc.
        double costoEnvio = 0;    // Implementar lógica real para el costo de envío.
        double totalCarrito = subtotal + costoEnvio; // Suma del subtotal y costo de envío.

        // Añade los datos calculados y los ítems al modelo para que la vista los renderice.
        model.addAttribute("items", items);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("totalEnviosCount", totalEnviosCount);
        model.addAttribute("costoEnvio", costoEnvio);
        model.addAttribute("totalCarrito", totalCarrito);

        // Retorna el nombre de la plantilla HTML del carrito.
        return "carrito";
    }

    /**
     * Maneja las solicitudes POST a la ruta "/carrito/actualizarCantidad".
     * Actualiza la cantidad de un producto específico en el carrito del usuario autenticado.
     *
     * @param productoId El ID del producto cuya cantidad se va a actualizar.
     * @param cantidad   La nueva cantidad deseada para el producto.
     * @param principal  Objeto {@link Principal} del usuario autenticado.
     * @return Una redirección a la página del carrito ("/carrito") para mostrar los cambios.
     * @throws java.util.NoSuchElementException Si el usuario autenticado no se encuentra.
     */
    @PostMapping("/actualizarCantidad")
    public String actualizarCantidad(@RequestParam Long productoId, @RequestParam int cantidad, Principal principal) {
        // Obtiene el usuario autenticado.
        Usuario usuario = usuarioRepository.findByEmail(principal.getName()).orElseThrow();
        // Obtiene o crea el carrito del usuario.
        Carrito carrito = carritoService.obtenerOCrearCarrito(usuario);
        // Delega la actualización de cantidad al servicio de carrito.
        carritoService.actualizarCantidad(carrito, productoId, cantidad);
        // Redirige de vuelta al carrito.
        return "redirect:/carrito";
    }

    /**
     * Maneja las solicitudes POST a la ruta "/carrito/agregar".
     * Agrega un producto al carrito del usuario autenticado. Si el producto ya existe,
     * incrementa su cantidad.
     *
     * @param productoId El ID del producto a agregar.
     * @param cantidad   La cantidad del producto a agregar (por defecto 1 si no se especifica).
     * @return Una redirección a la página del carrito ("/carrito") para mostrar el producto añadido.
     */
    @PostMapping("/agregar")
    public String agregarProducto(@RequestParam Long productoId, @RequestParam(defaultValue = "1") int cantidad) {
        // Obtiene el usuario actualmente autenticado (método auxiliar).
        Usuario usuario = getUsuarioActual();
        // Obtiene o crea el carrito para este usuario.
        Carrito carrito = carritoService.obtenerOCrearCarrito(usuario);
        // Delega la lógica de agregar producto al servicio de carrito.
        carritoService.agregarProducto(carrito, productoId, cantidad);
        // Redirige de vuelta al carrito.
        return "redirect:/carrito";
    }

    /**
     * Maneja las solicitudes POST a la ruta "/carrito/eliminar".
     * Elimina un producto específico del carrito del usuario autenticado.
     *
     * @param productoId El ID del producto a eliminar del carrito.
     * @return Una redirección a la página del carrito ("/carrito") para mostrar el carrito actualizado.
     */
    @PostMapping("/eliminar")
    public String eliminarProducto(@RequestParam Long productoId) {
        // Obtiene el usuario actualmente autenticado.
        Usuario usuario = getUsuarioActual();
        // Obtiene o crea el carrito del usuario.
        Carrito carrito = carritoService.obtenerOCrearCarrito(usuario);
        // Delega la eliminación del producto al servicio de carrito.
        carritoService.eliminarProducto(carrito, productoId);
        // Redirige de vuelta al carrito.
        return "redirect:/carrito";
    }

    /**
     * Maneja las solicitudes POST a la ruta "/carrito/vaciar".
     * Vacía completamente el carrito de compras del usuario autenticado, eliminando todos los ítems.
     *
     * @return Una redirección a la página del carrito ("/carrito"), que ahora estará vacía.
     */
    @PostMapping("/vaciar")
    public String vaciarCarrito() {
        // Obtiene el usuario actualmente autenticado.
        Usuario usuario = getUsuarioActual();
        // Obtiene o crea el carrito del usuario.
        Carrito carrito = carritoService.obtenerOCrearCarrito(usuario);
        // Delega el vaciado del carrito al servicio.
        carritoService.vaciarCarrito(carrito);
        // Redirige de vuelta al carrito.
        return "redirect:/carrito";
    }

    /**
     * Método auxiliar privado para obtener el objeto {@link Usuario} del usuario actualmente autenticado.
     * Utiliza {@link SecurityContextHolder} para acceder al contexto de seguridad de Spring.
     *
     * @return El objeto {@link Usuario} correspondiente al usuario que ha iniciado sesión.
     * @throws java.util.NoSuchElementException Si el usuario autenticado no se encuentra en el repositorio,
     * indicando un problema de datos o configuración de seguridad.
     */
    private Usuario getUsuarioActual() {
        // Obtiene el objeto Authentication del contexto de seguridad.
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // El nombre de usuario (email) se obtiene del objeto Authentication.
        String email = auth.getName();
        // Busca el usuario en el repositorio por su email. Lanza excepción si no se encuentra.
        return usuarioRepository.findByEmail(email).orElseThrow();
    }
}