package com.proyecto.prestigio.service;

import com.proyecto.prestigio.model.Cita;
import com.proyecto.prestigio.model.Usuario;

import java.util.List;

/**
 * Interfaz de servicio para la gestión de citas en la aplicación.
 * Define el contrato para las operaciones de negocio relacionadas con la entidad {@link Cita}.
 * Cualquier clase que implemente esta interfaz debe proporcionar una implementación para los métodos declarados aquí.
 * Esto promueve una arquitectura modular, la separación de responsabilidades y facilita el cambio de implementaciones de servicio.
 */
public interface CitaService {

    /**
     * Define la operación para agendar (guardar) una nueva cita en el sistema.
     *
     * @param cita El objeto {@link Cita} que se desea agendar.
     * @return El objeto {@link Cita} que ha sido persistido, el cual puede incluir el ID generado por la base de datos.
     */
    Cita agendarCita(Cita cita);

    /**
     * Define la operación para obtener una lista de todas las citas agendadas para un usuario específico.
     *
     * @param usuario El objeto {@link Usuario} del cual se desean obtener las citas.
     * @return Una {@link List} de objetos {@link Cita} asociados al usuario.
     */
    List<Cita> obtenerCitasDeUsuario(Usuario usuario);
}

