package com.proyecto.prestigio.service;

import com.proyecto.prestigio.model.Usuario;
import com.proyecto.prestigio.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementación del servicio de lógica de negocio para la gestión de usuarios.
 * Esta clase implementa la interfaz {@link UsuarioService} y proporciona las operaciones
 * básicas para interactuar con la entidad {@link Usuario}, como crear, obtener por ID
 * y obtener todos los usuarios.
 * Utiliza {@code @Service} para ser reconocido como un componente de servicio de Spring.
 */
@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    /**
     * Constructor para inyectar la dependencia del repositorio de usuarios.
     * Spring se encarga de proporcionar la instancia de {@link UsuarioRepository}.
     * La inyección por constructor es la forma preferida en Spring para manejar dependencias,
     * ya que facilita la inmutabilidad y la testabilidad.
     *
     * @param usuarioRepository Repositorio para acceder y gestionar los datos de los usuarios.
     */
    public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Crea y persiste un nuevo usuario en la base de datos.
     *
     * @param usuario El objeto {@link Usuario} a ser creado.
     * @return El objeto {@link Usuario} persistido, que incluirá el ID generado por la base de datos.
     * @Override indica que este método implementa un método de la interfaz {@link UsuarioService}.
     */
    @Override
    public Usuario crearUsuario(Usuario usuario) {
        // Delega la operación de guardado directamente al repositorio.
        return usuarioRepository.save(usuario);
    }

    /**
     * Obtiene un usuario de la base de datos por su identificador único.
     *
     * @param id El ID del usuario a buscar.
     * @return Un {@link Optional} que contendrá el objeto {@link Usuario} si se encuentra,
     * o un {@link Optional#empty()} si no existe un usuario con el ID dado.
     * @Override indica que este método implementa un método de la interfaz {@link UsuarioService}.
     */
    @Override
    public Optional<Usuario> obtenerPorId(Long id) {
        // Delega la operación de búsqueda por ID al repositorio.
        return usuarioRepository.findById(id);
    }

    /**
     * Obtiene una lista de todos los usuarios registrados en la base de datos.
     *
     * @return Una {@link List} de todos los objetos {@link Usuario} presentes en la base de datos.
     * @Override indica que este método implementa un método de la interfaz {@link UsuarioService}.
     */
    @Override
    public List<Usuario> obtenerTodos() {
        // Delega la operación de búsqueda de todos los usuarios al repositorio.
        return usuarioRepository.findAll();
    }
}