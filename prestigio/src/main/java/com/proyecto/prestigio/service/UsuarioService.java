package com.proyecto.prestigio.service;

import com.proyecto.prestigio.model.Usuario;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz de servicio para la gestión de usuarios en la aplicación.
 * Define el contrato para las operaciones de negocio relacionadas con la entidad {@link Usuario}.
 * Cualquier clase que implemente esta interfaz debe proporcionar una implementación para los métodos declarados aquí.
 * Esto promueve una arquitectura modular y facilita el cambio de implementaciones de servicio sin afectar a los consumidores del servicio.
 */
public interface UsuarioService {

    /**
     * Define la operación para crear y persistir un nuevo usuario.
     *
     * @param usuario El objeto {@link Usuario} que se desea crear.
     * @return El objeto {@link Usuario} que ha sido persistido, el cual puede incluir el ID generado por la base de datos.
     */
    Usuario crearUsuario(Usuario usuario);

    /**
     * Define la operación para obtener un usuario por su identificador único.
     *
     * @param id El ID del usuario a buscar.
     * @return Un {@link Optional} que contendrá el objeto {@link Usuario} si se encuentra,
     * o un {@link Optional#empty()} si no existe un usuario con el ID dado.
     */
    Optional<Usuario> obtenerPorId(Long id);

    /**
     * Define la operación para obtener una lista de todos los usuarios registrados en el sistema.
     *
     * @return Una {@link List} de todos los objetos {@link Usuario} presentes en la base de datos.
     */
    List<Usuario> obtenerTodos();
}

