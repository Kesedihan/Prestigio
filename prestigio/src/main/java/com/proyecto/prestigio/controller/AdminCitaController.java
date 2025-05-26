package com.proyecto.prestigio.controller;

import com.proyecto.prestigio.model.Cita;
import com.proyecto.prestigio.repository.CitaRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controlador de Spring MVC para la administración de citas.
 * Este controlador maneja las solicitudes relacionadas con la visualización y actualización
 * del estado de las citas por parte de un usuario con rol de administrador.
 * La ruta base para todas las operaciones de este controlador es "/admin/citas".
 * Utiliza {@code @Controller} para trabajar con vistas (plantillas HTML) y servir la interfaz de administración.
 */
@Controller
@RequestMapping("/admin/citas")
public class AdminCitaController {

    private final CitaRepository citaRepository;

    /**
     * Constructor para inyectar la dependencia del repositorio de citas.
     * Spring se encarga de proporcionar la instancia de {@link CitaRepository}.
     *
     * @param citaRepository Repositorio para acceder y gestionar los datos de las citas.
     */
    public AdminCitaController(CitaRepository citaRepository) {
        this.citaRepository = citaRepository;
    }

    /**
     * Maneja las solicitudes GET a la ruta base "/admin/citas".
     * Este método recupera todas las citas de la base de datos, ordenadas por fecha ascendente,
     * y las añade al modelo para ser mostradas en la vista de administración de citas.
     *
     * @param model Objeto {@link Model} de Spring UI para añadir atributos que serán utilizados por la vista.
     * @return El nombre de la vista ("admin-citas.html") que muestra el listado de todas las citas.
     */
    @GetMapping
    public String listarCitas(Model model) {
        // Recupera todas las citas de la base de datos y las ordena por fecha de forma ascendente.
        model.addAttribute("citas", citaRepository.findAllByOrderByFechaAsc());
        // Retorna el nombre de la plantilla HTML a renderizar para mostrar las citas al administrador.
        return "admin-citas";
    }

    /**
     * Maneja las solicitudes POST a la ruta "/admin/citas/estado" para actualizar el estado de una cita.
     * Este método recibe el ID de una cita y el nuevo estado, busca la cita, actualiza su estado
     * y la guarda en la base de datos. Finalmente, redirige de vuelta a la lista de citas.
     *
     * @param citaId El ID de la cita cuyo estado se desea actualizar.
     * @param estado La nueva cadena de estado para la cita (ej. "APROBADA", "CANCELADA", "COMPLETADA").
     * @return Una redirección a la página de listado de citas ("/admin/citas") después de la actualización.
     * @throws java.util.NoSuchElementException Si la cita con el {@code citaId} proporcionado no se encuentra,
     * lo cual debería ser manejado adecuadamente en un entorno de producción (ej. con un mensaje de error).
     */
    @PostMapping("/estado")
    public String actualizarEstado(@RequestParam Long citaId, @RequestParam String estado) {
        // Busca la cita en la base de datos por su ID. Si no la encuentra, lanza una excepción.
        Cita cita = citaRepository.findById(citaId).orElseThrow();
        // Establece el nuevo estado de la cita.
        cita.setEstado(estado);
        // Guarda la cita actualizada en la base de datos.
        citaRepository.save(cita);
        // Redirige al administrador a la página de listado de citas para ver los cambios.
        return "redirect:/admin/citas";
    }
}