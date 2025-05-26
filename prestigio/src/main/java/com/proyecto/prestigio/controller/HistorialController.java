package com.proyecto.prestigio.controller;

import com.proyecto.prestigio.model.Usuario;
import com.proyecto.prestigio.service.VentaService;
import com.proyecto.prestigio.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controlador de Spring MVC que podría estar diseñado para gestionar el historial
 * y la sección de "Mi Cuenta" de un usuario autenticado.
 *
 * NOTA IMPORTANTE: A pesar de tener un {@code @RequestMapping("/mi-cuenta")} a nivel de clase,
 * este controlador, tal como está presentado, no contiene ningún método {@code @GetMapping}
 * o {@code @PostMapping} que sirva una ruta web. Esto significa que, actualmente, no expone
 * ninguna funcionalidad directamente al navegador.
 *
 * Es posible que los métodos relacionados con la visualización del historial y la información
 * de la cuenta se encuentren en otro controlador (como {@link CuentaController}) o que este
 * controlador esté incompleto y a la espera de que se le añadan dichos métodos.
 *
 * Utiliza {@code @Controller} para indicar que es un componente de la capa de presentación.
 */
@Controller
@RequestMapping("/mi-cuenta")
public class HistorialController {

    private final VentaService ventaService;
    private final UsuarioRepository usuarioRepository;

    /**
     * Constructor para inyectar las dependencias del servicio de ventas y el repositorio de usuarios.
     * Spring se encarga de proporcionar las instancias (beans) de estos componentes.
     *
     * @param ventaService    El servicio que gestiona la lógica de negocio relacionada con las ventas,
     * incluyendo la obtención del historial de compras de un usuario.
     * @param usuarioRepository Repositorio para acceder y gestionar los datos de los usuarios,
     * esencial para obtener el usuario autenticado.
     */
    public HistorialController(VentaService ventaService, UsuarioRepository usuarioRepository) {
        this.ventaService = ventaService;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Método auxiliar privado para obtener el objeto {@link Usuario} del usuario actualmente autenticado.
     * Es utilizado por otros métodos dentro de controladores que requieren el contexto del usuario logueado.
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