package com.proyecto.prestigio.controller;

import com.proyecto.prestigio.model.Cita;
import com.proyecto.prestigio.model.Usuario;
import com.proyecto.prestigio.repository.CitaRepository;
import com.proyecto.prestigio.repository.UsuarioRepository;
import com.proyecto.prestigio.service.VentaService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controlador de Spring MVC que gestiona la sección de "Mi Cuenta" para los usuarios.
 * Permite a los usuarios autenticados ver su información personal, el historial de sus citas
 * y el historial de sus compras (ventas). También ofrece la funcionalidad para cancelar citas.
 * La ruta principal para este controlador es "/mi-cuenta".
 * Utiliza {@code @Controller} para servir vistas HTML (Thymeleaf) a los usuarios.
 */
@Controller
public class CuentaController {

    private final UsuarioRepository usuarioRepository;
    private final CitaRepository citaRepository;
    private final VentaService ventaService;

    /**
     * Constructor para inyectar las dependencias de los repositorios y servicios necesarios.
     * Spring se encarga de proporcionar las instancias (beans) de estos componentes.
     *
     * @param usuarioRepository Repositorio para acceder y gestionar los datos de los usuarios.
     * @param citaRepository    Repositorio para acceder y gestionar los datos de las citas.
     * @param ventaService      Servicio que gestiona la lógica de negocio relacionada con las ventas.
     */
    public CuentaController(UsuarioRepository usuarioRepository, CitaRepository citaRepository, VentaService ventaService) {
        this.usuarioRepository = usuarioRepository;
        this.citaRepository = citaRepository;
        this.ventaService = ventaService;
    }

    /**
     * Maneja las solicitudes GET a la ruta "/mi-cuenta".
     * Este método carga la información del usuario autenticado, sus citas y su historial de compras,
     * y la añade al modelo para ser mostrada en la vista de la cuenta del usuario.
     *
     * @param model Objeto {@link Model} de Spring UI para añadir atributos que serán utilizados por la vista.
     * @return El nombre de la vista ("mi-cuenta.html") que muestra el perfil del usuario.
     */
    @GetMapping("/mi-cuenta")
    public String verMiCuenta(Model model) {
        // Obtiene el objeto Authentication del contexto de seguridad de Spring.
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Verifica si hay un usuario autenticado y no es el usuario anónimo por defecto de Spring Security.
        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            // El nombre de autenticación es el email del usuario en tu configuración de seguridad.
            String email = auth.getName();
            // Busca el usuario en la base de datos por su email.
            Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);

            // Si el usuario se encuentra (lo cual debería ser el caso para un usuario autenticado)
            if (usuario != null) {
                // Añade el objeto Usuario al modelo.
                model.addAttribute("usuario", usuario);
                // Añade la lista de citas del usuario, ordenadas por fecha descendente, al modelo.
                model.addAttribute("citas", citaRepository.findByUsuarioOrderByFechaDesc(usuario));
                // Añade el historial de compras (ventas) del usuario, obtenido a través del servicio de ventas, al modelo.
                model.addAttribute("ventas", ventaService.obtenerHistorialPorUsuario(usuario));
            }
        }
        // Retorna el nombre de la plantilla HTML a renderizar para la página "mi-cuenta".
        return "mi-cuenta";
    }

    /**
     * Maneja las solicitudes POST a la ruta "/cliente/cancelar-cita".
     * Permite a un usuario cliente cancelar una de sus citas. La cita solo se puede cancelar si está en estado "PENDIENTE".
     * Si la cita existe y está en estado "PENDIENTE", su estado se actualiza a "CANCELADA".
     *
     * @param citaId El ID de la cita que el usuario desea cancelar.
     * @return Una redirección a la página de "mi cuenta" ("/mi-cuenta") después de intentar cancelar la cita.
     */
    @PostMapping("/cliente/cancelar-cita")
    public String cancelarCita(@RequestParam Long citaId) {
        // Busca la cita en la base de datos por su ID. Si no se encuentra, 'cita' será null.
        Cita cita = citaRepository.findById(citaId).orElse(null);

        // Verifica si la cita existe y si su estado actual es "PENDIENTE".
        if (cita != null && cita.getEstado().equals("PENDIENTE")) {
            // Si las condiciones se cumplen, cambia el estado de la cita a "CANCELADA".
            cita.setEstado("CANCELADA");
            // Guarda los cambios en la base de datos.
            citaRepository.save(cita);
        }
        // Redirige al usuario de vuelta a la página de "mi cuenta" para que vea el estado actualizado.
        return "redirect:/mi-cuenta";
    }
}