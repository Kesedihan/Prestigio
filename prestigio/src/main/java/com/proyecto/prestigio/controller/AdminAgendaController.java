package com.proyecto.prestigio.controller;


import com.proyecto.prestigio.model.Cita;
import com.proyecto.prestigio.model.Disponibilidad;
import com.proyecto.prestigio.model.Servicio;
import com.proyecto.prestigio.model.Usuario;
import com.proyecto.prestigio.repository.CitaRepository;
import com.proyecto.prestigio.repository.DisponibilidadRepository;
import com.proyecto.prestigio.repository.ServicioRepository;
import com.proyecto.prestigio.repository.UsuarioRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Controlador de Spring MVC para la gestión de la agenda por parte del administrador.
 * Este controlador ofrece funcionalidades para:
 * <ul>
 * <li>Mostrar la vista de administración de la agenda.</li>
 * <li>Agregar nuevos servicios.</li>
 * <li>Agregar nuevas franjas de disponibilidad para los servicios.</li>
 * <li>Agendar citas directamente para los clientes.</li>
 * </ul>
 * La ruta base para todas las operaciones en este controlador es "/admin/agenda".
 * Utiliza {@code @Controller} para trabajar con vistas (plantillas HTML) y servir la interfaz de administración.
 */
@Controller
@RequestMapping("/admin/agenda")
public class AdminAgendaController {

    private final ServicioRepository servicioRepository;
    private final DisponibilidadRepository disponibilidadRepository;
    private final UsuarioRepository usuarioRepository;
    private final CitaRepository citaRepository;

    /**
     * Constructor para inyectar las dependencias de los repositorios necesarios.
     * Spring se encarga de proporcionar las instancias (beans) de estos repositorios.
     *
     * @param servicioRepository       Repositorio para acceder y gestionar los servicios.
     * @param disponibilidadRepository Repositorio para acceder y gestionar las disponibilidades de citas.
     * @param usuarioRepository        Repositorio para acceder y gestionar la información de los usuarios.
     * @param citaRepository           Repositorio para acceder y gestionar las citas agendadas.
     */
    public AdminAgendaController(ServicioRepository servicioRepository,
                                 DisponibilidadRepository disponibilidadRepository,
                                 UsuarioRepository usuarioRepository,
                                 CitaRepository citaRepository) {
        this.servicioRepository = servicioRepository;
        this.disponibilidadRepository = disponibilidadRepository;
        this.usuarioRepository = usuarioRepository;
        this.citaRepository = citaRepository;
    }

    /**
     * Maneja las solicitudes GET a la ruta base "/admin/agenda".
     * Este método carga los datos necesarios (servicios, disponibilidades, usuarios)
     * para la vista de administración de la agenda y los añade al modelo.
     *
     * @param model Objeto {@link Model} de Spring UI para añadir atributos que serán utilizados por la vista.
     * @return El nombre de la vista ("admin-agenda.html") que muestra la interfaz de gestión de la agenda.
     */
    @GetMapping
    public String mostrarAgenda(Model model) {
        // Añade todos los servicios al modelo para ser mostrados en la vista.
        model.addAttribute("servicios", servicioRepository.findAll());
        // Añade solo las disponibilidades que están marcadas como disponibles al modelo.
        model.addAttribute("disponibilidades", disponibilidadRepository.findByDisponibleTrue());
        // Añade todos los usuarios al modelo, probablemente para la función de agendar para un cliente.
        model.addAttribute("usuarios", usuarioRepository.findAll());
        // Retorna el nombre de la plantilla HTML a renderizar.
        return "admin-agenda";
    }

    /**
     * Maneja las solicitudes POST a la ruta "/admin/agenda/servicio" para agregar un nuevo servicio.
     * Este método recibe los detalles del servicio a través de parámetros de solicitud,
     * crea una nueva instancia de Servicio y la guarda en la base de datos.
     *
     * @param nombre      El nombre del nuevo servicio.
     * @param descripcion La descripción del nuevo servicio.
     * @param duracion    La duración estimada del servicio en minutos.
     * @param precio      El precio del servicio.
     * @return Una redirección a la página de administración de la agenda ("/admin/agenda") después de agregar el servicio.
     */
    @PostMapping("/servicio")
    public String agregarServicio(@RequestParam String nombre,
                                  @RequestParam String descripcion,
                                  @RequestParam Integer duracion,
                                  @RequestParam BigDecimal precio) {
        Servicio servicio = new Servicio();
        servicio.setNombre(nombre);
        servicio.setDescripcion(descripcion);
        servicio.setDuracion(duracion);
        servicio.setPrecio(precio);
        servicio.setEstado(true); // Por defecto, un servicio nuevo se establece como activo.
        servicioRepository.save(servicio); // Guarda el nuevo servicio en la base de datos.
        return "redirect:/admin/agenda"; // Redirige de vuelta a la vista de la agenda.
    }

    /**
     * Maneja las solicitudes POST a la ruta "/admin/agenda/disponibilidad" para agregar una nueva disponibilidad.
     * Este método asocia una nueva franja horaria a un servicio existente y la marca como disponible.
     *
     * @param servicioId El ID del servicio al que se asociará esta disponibilidad.
     * @param fechaHora  La fecha y hora específica de la nueva disponibilidad. Se espera en formato ISO 8601 (yyyy-MM-dd'T'HH:mm:ss).
     * @return Una redirección a la página de administración de la agenda ("/admin/agenda") después de agregar la disponibilidad.
     * @throws java.util.NoSuchElementException Si el servicio con el {@code servicioId} proporcionado no se encuentra.
     */
    @PostMapping("/disponibilidad")
    public String agregarDisponibilidad(@RequestParam Long servicioId,
                                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaHora) {
        // Busca el servicio al que se asociará esta disponibilidad. Lanza excepción si no se encuentra.
        Servicio servicio = servicioRepository.findById(servicioId).orElseThrow();
        // Crea una nueva instancia de Disponibilidad, asociándola al servicio y la fecha/hora, y marcándola como disponible.
        Disponibilidad d = new Disponibilidad(null, servicio, fechaHora, true);
        disponibilidadRepository.save(d); // Guarda la nueva disponibilidad en la base de datos.
        return "redirect:/admin/agenda"; // Redirige de vuelta a la vista de la agenda.
    }

    /**
     * Maneja las solicitudes POST a la ruta "/admin/agenda/agendar" para agendar una cita para un cliente.
     * Este método permite al administrador crear una cita directamente para un usuario y una disponibilidad específica.
     * Verifica la disponibilidad antes de agendar y marca la franja horaria como no disponible.
     *
     * @param usuarioId        El ID del usuario para quien se agendará la cita.
     * @param disponibilidadId El ID de la disponibilidad seleccionada para la cita.
     * @return Una redirección a la página de administración de la agenda.
     * Si la disponibilidad ya está ocupada, redirige con un parámetro de error.
     * Si se agendó con éxito, redirige con un parámetro de éxito.
     * @throws java.util.NoSuchElementException Si el usuario o la disponibilidad con los IDs proporcionados no se encuentran.
     */
    @PostMapping("/agendar")
    public String agendarParaCliente(@RequestParam Long usuarioId,
                                     @RequestParam Long disponibilidadId) {
        // Busca el usuario y la disponibilidad en la base de datos. Lanzan excepción si no se encuentran.
        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow();
        Disponibilidad disponibilidad = disponibilidadRepository.findById(disponibilidadId).orElseThrow();

        // Verifica si la disponibilidad seleccionada ya ha sido tomada.
        if (!disponibilidad.isDisponible()) {
            return "redirect:/admin/agenda?error=ocupado"; // Si está ocupada, redirige con error.
        }

        // Marca la disponibilidad como no disponible para evitar dobles reservas.
        disponibilidad.setDisponible(false);
        disponibilidadRepository.save(disponibilidad); // Guarda el cambio de estado de la disponibilidad.

        // Crea una nueva instancia de Cita y establece sus propiedades.
        Cita cita = new Cita();
        cita.setUsuario(usuario);
        cita.setServicio(disponibilidad.getServicio()); // El servicio es el mismo que el de la disponibilidad.
        cita.setFecha(disponibilidad.getFechaHora());   // La fecha/hora de la cita es la de la disponibilidad.
        cita.setEstado("PENDIENTE"); // El estado inicial de la cita agendada por admin es "PENDIENTE".

        citaRepository.save(cita); // Guarda la nueva cita en la base de datos.

        return "redirect:/admin/agenda?agendado=ok"; // Redirige con un indicador de éxito.
    }

}