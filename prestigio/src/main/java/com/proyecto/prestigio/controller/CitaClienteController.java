package com.proyecto.prestigio.controller;

import com.proyecto.prestigio.model.Cita;
import com.proyecto.prestigio.model.Disponibilidad;
import com.proyecto.prestigio.model.Servicio;
import com.proyecto.prestigio.model.Usuario;
import com.proyecto.prestigio.repository.CitaRepository;
import com.proyecto.prestigio.repository.DisponibilidadRepository;
import com.proyecto.prestigio.repository.ServicioRepository;
import com.proyecto.prestigio.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador de Spring MVC para gestionar las operaciones relacionadas con las citas del cliente.
 * Este controlador maneja las solicitudes para mostrar el formulario de agendamiento de citas,
 * así como para procesar la creación de nuevas citas por parte de los usuarios.
 * La ruta base para todas las solicitudes en este controlador es "/cliente".
 * Utiliza {@code @Controller} para servir vistas HTML (Thymeleaf en este caso).
 */
@Controller
@RequestMapping("/cliente")
public class CitaClienteController {

    private final DisponibilidadRepository disponibilidadRepository;
    private final UsuarioRepository usuarioRepository;
    private final CitaRepository citaRepository;
    private final ServicioRepository servicioRepository;

    /**
     * Constructor para inyectar las dependencias de los repositorios necesarios.
     * Spring se encarga automáticamente de proveer las instancias de estos repositorios.
     *
     * @param disponibilidadRepository Repositorio para acceder y gestionar las disponibilidades de citas.
     * @param usuarioRepository        Repositorio para acceder y gestionar la información de los usuarios.
     * @param citaRepository           Repositorio para acceder y gestionar las citas agendadas.
     * @param servicioRepository       Repositorio para acceder y gestionar los servicios ofrecidos.
     */
    public CitaClienteController(DisponibilidadRepository disponibilidadRepository,
                                 UsuarioRepository usuarioRepository,
                                 CitaRepository citaRepository,
                                 ServicioRepository servicioRepository) {
        this.disponibilidadRepository = disponibilidadRepository;
        this.usuarioRepository = usuarioRepository;
        this.citaRepository = citaRepository;
        this.servicioRepository = servicioRepository;
    }

    /**
     * Maneja las solicitudes GET para mostrar el formulario de agendamiento de citas para el cliente.
     * Este método recupera los servicios disponibles, las disponibilidades de citas (filtradas por servicio si se especifica)
     * y las citas pendientes del usuario autenticado, para luego pasarlas a la vista.
     *
     * @param servicioId Parámetro opcional para filtrar las disponibilidades por un servicio específico.
     * Si es nulo, se muestran todas las disponibilidades disponibles.
     * @param model      Objeto {@link Model} de Spring UI para añadir atributos que serán utilizados por la vista.
     * @return El nombre de la vista ("agendar-cita.html") que muestra el formulario y las listas de datos.
     */
    @GetMapping("/agendar")
    public String mostrarFormularioAgendar(@RequestParam(required = false) Long servicioId, Model model) {
        // Obtiene la información de autenticación del usuario actual desde el contexto de seguridad de Spring.
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // El nombre de autenticación en este contexto es el email del usuario.
        String email = auth.getName();
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);

        // Recupera todos los servicios activos (estado=true)
        List<Servicio> servicios = servicioRepository.findByEstadoTrue();

        List<Disponibilidad> disponibilidades;
        // Si se proporciona un 'servicioId', filtra las disponibilidades por ese servicio y que estén disponibles.
        if (servicioId != null) {
            disponibilidades = disponibilidadRepository.findByDisponibleTrueAndServicioId(servicioId);
        } else {
            // Si no se proporciona 'servicioId', obtiene todas las disponibilidades que estén disponibles.
            disponibilidades = disponibilidadRepository.findByDisponibleTrue();
        }

        // Recupera las citas del usuario actual que están en estado "PENDIENTE".
        List<Cita> citasPendientes = citaRepository.findByUsuarioAndEstado(usuario, "PENDIENTE");

        // Añade los datos al modelo para que sean accesibles desde la plantilla Thymeleaf.
        model.addAttribute("servicios", servicios);
        model.addAttribute("disponibilidades", disponibilidades);
        model.addAttribute("citasPendientes", citasPendientes);
        model.addAttribute("servicioSeleccionado", servicioId); // Se usa para preseleccionar el servicio en la vista

        // Retorna el nombre de la plantilla HTML a renderizar.
        return "agendar-cita";
    }

    /**
     * Maneja las solicitudes POST para guardar una nueva cita agendada por el cliente.
     * Este método recibe el ID de una disponibilidad seleccionada, la marca como no disponible,
     * y crea una nueva entrada en la tabla de citas para el usuario autenticado.
     *
     * @param disponibilidadId El ID de la {@link Disponibilidad} seleccionada por el usuario.
     * @return Una redirección a la página de "mi cuenta" con un indicador de éxito si la cita se agenda correctamente.
     * Si la disponibilidad seleccionada ya no está disponible, redirige de nuevo al formulario de agendar
     * con un parámetro de error.
     * @throws java.util.NoSuchElementException Si el usuario autenticado o la disponibilidad no se encuentran,
     * lo cual debería ser manejado a un nivel superior o con un mejor control de excepciones.
     */
    @PostMapping("/agendar")
    public String guardarCita(@RequestParam Long disponibilidadId) {
        // Obtiene la información de autenticación del usuario actual.
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        // Busca el usuario por email. Si no lo encuentra, lanza una excepción (RuntimeException).
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow();

        // Busca la disponibilidad por su ID. Si no la encuentra, lanza una excepción.
        Disponibilidad disponibilidad = disponibilidadRepository.findById(disponibilidadId).orElseThrow();

        // Verifica si la disponibilidad ya ha sido tomada por otro usuario o proceso.
        if (!disponibilidad.isDisponible()) {
            // Si ya está ocupada, redirige con un mensaje de error.
            return "redirect:/cliente/agendar?error=ocupada";
        }

        // Marca la disponibilidad como no disponible en la base de datos para evitar doble agendamiento.
        disponibilidad.setDisponible(false);
        disponibilidadRepository.save(disponibilidad);

        // Crea una nueva instancia de Cita.
        Cita cita = new Cita();
        // Asigna el usuario autenticado a la cita.
        cita.setUsuario(usuario);
        // Asigna el servicio asociado a la disponibilidad seleccionada.
        cita.setServicio(disponibilidad.getServicio());
        // Asigna la fecha y hora de la disponibilidad como la fecha de la cita.
        cita.setFecha(disponibilidad.getFechaHora());
        // Establece el estado inicial de la cita como "PENDIENTE".
        cita.setEstado("PENDIENTE");

        // Guarda la nueva cita en la base de datos.
        citaRepository.save(cita);

        // Redirige al usuario a la página de "mi cuenta" con un indicador de que la cita fue agendada exitosamente.
        return "redirect:/mi-cuenta?agendada=ok";
    }
}