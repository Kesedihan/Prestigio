package com.proyecto.prestigio.service;

import com.proyecto.prestigio.model.Rol;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz de servicio que define las operaciones de negocio para la gestión de roles de usuario.
 * Esta interfaz establece un contrato claro y una API para interactuar con la entidad {@link Rol},
 * promoviendo la cohesión de la capa de servicio y la independencia de la implementación concreta.
 * Al interactuar con esta interfaz en lugar de una clase de implementación directa, se logra
 * un mayor desacoplamiento y flexibilidad en la arquitectura de la aplicación.
 */
public interface RolService {

    /**
     * Define la operación para crear y persistir un nuevo objeto de rol en el sistema.
     * @param rol El objeto {@link Rol} que contiene la información del nuevo rol a ser creado.
     * @return El objeto {@link Rol} persistido por la base de datos, incluyendo cualquier ID generado
     * y otros metadatos de persistencia.
     */
    Rol crearRol(Rol rol);

    /**
     * Define la operación para buscar y obtener un rol por su nombre único.
     * El nombre del rol es típicamente una cadena que lo identifica, como "ADMIN" o "USUARIO".
     *
     * @param nombre La cadena de texto que representa el nombre del rol a buscar.
     * @return Un objeto {@link Optional} que contendrá el {@link Rol} si se encuentra un rol
     * con el nombre especificado; de lo contrario, retornará un {@link Optional#empty()}.
     * Esto permite un manejo seguro de la posible ausencia del rol.
     */
    Optional<Rol> obtenerPorNombre(String nombre);

    /**
     * Define la operación para recuperar una lista de todos los roles disponibles en el sistema.
     *
     * @return Una {@link List} que contiene todos los objetos {@link Rol} existentes en la base de datos.
     * La lista estará vacía si no hay roles registrados.
     */
    List<Rol> obtenerTodos();
}

