package com.proyecto.prestigio.service;

import com.proyecto.prestigio.model.Rol;
import com.proyecto.prestigio.repository.RolRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementación del servicio de lógica de negocio para la gestión de roles.
 * Esta clase implementa la interfaz {@link RolService} y proporciona las operaciones
 * básicas para interactuar con la entidad {@link Rol}, como crear, obtener por nombre
 * y obtener todos los roles.
 * Utiliza {@code @Service} para ser reconocido como un componente de servicio de Spring.
 */
@Service
public class RolServiceImpl implements RolService {

    private final RolRepository rolRepository;

    /**
     * Constructor para inyectar la dependencia del repositorio de roles.
     * Spring se encarga de proporcionar la instancia de {@link RolRepository}.
     * La inyección por constructor es la forma preferida en Spring para manejar dependencias.
     *
     * @param rolRepository Repositorio para acceder y gestionar los datos de los roles.
     */
    public RolServiceImpl(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    /**
     * Crea y persiste un nuevo rol en la base de datos.
     *
     * @param rol El objeto {@link Rol} a ser creado.
     * @return El objeto {@link Rol} persistido, que incluirá el ID generado por la base de datos.
     * @Override indica que este método implementa un método de la interfaz {@link RolService}.
     */
    @Override
    public Rol crearRol(Rol rol) {
        // Delega la operación de guardado directamente al repositorio.
        return rolRepository.save(rol);
    }

    /**
     * Obtiene un rol de la base de datos por su nombre.
     *
     * @param nombre El nombre del rol a buscar.
     * @return Un {@link Optional} que contendrá el objeto {@link Rol} si se encuentra,
     * o un {@link Optional#empty()} si no existe un rol con el nombre dado.
     * @Override indica que este método implementa un método de la interfaz {@link RolService}.
     */
    @Override
    public Optional<Rol> obtenerPorNombre(String nombre) {
        // Delega la operación de búsqueda por nombre al repositorio.
        return rolRepository.findByNombre(nombre);
    }

    /**
     * Obtiene una lista de todos los roles registrados en la base de datos.
     *
     * @return Una {@link List} de todos los objetos {@link Rol} presentes en la base de datos.
     * @Override indica que este método implementa un método de la interfaz {@link RolService}.
     */
    @Override
    public List<Rol> obtenerTodos() {
        // Delega la operación de búsqueda de todos los roles al repositorio.
        return rolRepository.findAll();
    }
}