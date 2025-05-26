package com.proyecto.prestigio.controller;

import com.proyecto.prestigio.model.Carrito;
import com.proyecto.prestigio.model.CarritoItem;
import com.proyecto.prestigio.model.Usuario;
import com.proyecto.prestigio.service.CarritoService;
import com.proyecto.prestigio.service.VentaService;
import com.proyecto.prestigio.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador de Spring MVC para gestionar el proceso de compra de productos.
 * Este controlador es responsable de recibir la información necesaria para una compra (dirección, tipo de pago,
 * productos seleccionados del carrito) y coordinar el proceso de registro de la venta,
 * la validación de stock y la actualización del carrito.
 * La ruta base para las operaciones de compra es "/comprar".
 * Utiliza {@code @Controller} para trabajar con redirecciones y atributos flash en el contexto de un flujo web.
 */
@Controller
@RequestMapping("/comprar")
public class CompraController {

    private final VentaService ventaService;
    private final UsuarioRepository usuarioRepository;
    private final CarritoService carritoService;

    /**
     * Constructor para inyectar las dependencias del servicio de ventas, repositorio de usuarios y servicio de carrito.
     * Spring se encarga de proporcionar las instancias (beans) de estos componentes.
     *
     * @param ventaService    El servicio que gestiona la lógica de negocio relacionada con las ventas.
     * @param usuarioRepository Repositorio para acceder a los datos de los usuarios.
     * Es crucial para obtener el usuario autenticado.
     * @param carritoService  El servicio que gestiona la lógica de negocio del carrito de compras.
     */
    public CompraController(VentaService ventaService, UsuarioRepository usuarioRepository, CarritoService carritoService) {
        this.ventaService = ventaService;
        this.usuarioRepository = usuarioRepository;
        this.carritoService = carritoService;
    }

    /**
     * Maneja las solicitudes POST a la ruta base "/comprar".
     * Este método procesa la finalización de una compra por parte del usuario autenticado.
     * Incluye la validación de stock, el registro de la venta y la eliminación de los ítems comprados del carrito.
     *
     * @param direccion        La dirección de envío proporcionada por el usuario.
     * @param tipoPago         El método de pago seleccionado por el usuario.
     * @param selectedProducts Una cadena de texto que contiene los IDs de los productos seleccionados del carrito,
     * separados por comas (ej. "1,5,8").
     * @param principal        Objeto {@link Principal} que representa al usuario autenticado.
     * @param redirectAttributes Permite añadir atributos que estarán disponibles después de una redirección,
     * útil para mensajes de error o éxito.
     * @return Una redirección a la página del carrito si hay un error de stock, o a la página de "mi cuenta"
     * con un indicador de compra exitosa.
     * @throws java.util.NoSuchElementException Si el usuario autenticado no se encuentra en la base de datos.
     */
    @PostMapping
    public String comprar(@RequestParam("direccion") String direccion,
                          @RequestParam("tipoPago") String tipoPago,
                          @RequestParam("selectedProducts") String selectedProducts,
                          Principal principal,
                          RedirectAttributes redirectAttributes) {

        // Obtiene el usuario autenticado utilizando el email del Principal.
        Usuario usuario = usuarioRepository.findByEmail(principal.getName()).orElseThrow();
        // Obtiene o crea el carrito para el usuario.
        Carrito carrito = carritoService.obtenerOCrearCarrito(usuario);

        // Parsea la cadena de IDs de productos seleccionados en una lista de Long.
        // Se utiliza stream para dividir la cadena por comas, filtrar entradas vacías y convertir a Long.
        List<Long> productosSeleccionados = Arrays.stream(selectedProducts.split(","))
                .filter(s -> !s.isEmpty()) // Filtra cadenas vacías que pueden resultar de comas extra (ej. "1,,2")
                .map(Long::parseLong)      // Convierte cada cadena a Long
                .collect(Collectors.toList());

        // Filtra los CarritoItem del carrito del usuario para incluir solo los productos que fueron seleccionados.
        List<CarritoItem> itemsSeleccionados = carrito.getItems().stream()
                .filter(item -> productosSeleccionados.contains(item.getProducto().getId()))
                .collect(Collectors.toList());

        // *** Lógica de Validación de Stock ***
        for (CarritoItem item : itemsSeleccionados) {
            int stockDisponible = item.getProducto().getStock();
            // Si la cantidad solicitada excede el stock disponible para algún producto, se aborta la compra.
            if (item.getCantidad() > stockDisponible) {
                // Añade un atributo flash (que solo estará disponible para la próxima redirección) con el mensaje de error.
                redirectAttributes.addFlashAttribute("errorStock",
                        "No hay suficiente stock para el producto: " + item.getProducto().getNombre() + ". Stock disponible: " + stockDisponible + ", Cantidad solicitada: " + item.getCantidad());
                // Redirige al usuario de vuelta a la página del carrito.
                return "redirect:/carrito";
            }
        }

        // Si la validación de stock es exitosa, se procede a registrar la compra.
        // Delega la lógica de negocio de la venta al servicio de ventas, incluyendo solo los ítems seleccionados.
        ventaService.registrarCompraConItems(usuario, direccion, tipoPago, itemsSeleccionados);

        // *** Actualización del Carrito ***
        // Elimina del carrito solo los ítems que fueron parte de la compra exitosa.
        carritoService.eliminarItems(carrito, productosSeleccionados);

        // Redirige al usuario a la página de "mi cuenta" con un indicador de compra exitosa.
        return "redirect:/mi-cuenta?compraExitosa";
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