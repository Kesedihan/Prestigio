package com.proyecto.prestigio.controller;

import com.proyecto.prestigio.model.Rol;
import com.proyecto.prestigio.model.Usuario;
import com.proyecto.prestigio.repository.RolRepository;
import com.proyecto.prestigio.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * Controlador para manejar las operaciones de registro de nuevos usuarios en la aplicación.
 * Este controlador se encarga de servir el formulario de registro y procesar las solicitudes
 * para crear nuevas cuentas de usuario.
 * Utiliza {@code @Controller} porque está diseñado para trabajar con vistas de Thymeleaf y no es una API REST.
 * La anotación {@code @Transactional} asegura que la operación de guardar el usuario y, potencialmente, el rol,
 * se realice dentro de una transacción de base de datos.
 */
@Controller
@Transactional
public class RegistroController {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * Constructor para inyectar las dependencias necesarias.
     *
     * @param usuarioRepository Repositorio para interactuar con la base de datos para la entidad Usuario.
     * @param rolRepository     Repositorio para interactuar con la base de datos para la entidad Rol.
     * @param encoder           Codificador de contraseñas para encriptar las contraseñas de los usuarios.
     */
    public RegistroController(UsuarioRepository usuarioRepository, RolRepository rolRepository, BCryptPasswordEncoder encoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = encoder;
    }

    /**
     * Procesa las solicitudes POST para la ruta "/registro", utilizadas para registrar un nuevo usuario.
     * Recoge los datos del usuario enviados desde el formulario, verifica si el correo electrónico ya existe,
     * asigna el rol "CLIENTE" (creándolo si no existe), encripta la contraseña y guarda el nuevo usuario.
     *
     * @param nombre   El nombre del usuario.
     * @param apellido El apellido del usuario.
     * @param email    El correo electrónico del usuario, que debe ser único.
     * @param telefono El número de teléfono del usuario.
     * @param password La contraseña proporcionada por el usuario, que será encriptada.
     * @return Una redirección a la página de inicio ("/") si el registro es exitoso.
     * En caso de que el correo electrónico ya esté registrado, redirige de nuevo al formulario de registro
     * con un parámetro de error.
     */
    @PostMapping("/registro")
    public String registrarUsuario(@RequestParam String nombre,
                                   @RequestParam String apellido,
                                   @RequestParam String email,
                                   @RequestParam String telefono,
                                   @RequestParam String password) {

        // Verifica si ya existe un usuario con el mismo correo electrónico
        if (usuarioRepository.findByEmail(email).isPresent()) {
            // Si el email ya existe, redirige al formulario de registro con un indicador de error
            return "redirect:/registro?error=email";
        }

        // Busca el rol "CLIENTE". Si no existe, lo crea y lo guarda en la base de datos.
        // Esto asegura que el rol "CLIENTE" siempre esté disponible para nuevos registros.
        Rol rolCliente = rolRepository.findByNombre("CLIENTE")
                .orElseGet(() -> rolRepository.save(new Rol(null, "CLIENTE", null)));

        // Crea una nueva instancia de Usuario y establece sus propiedades con los datos recibidos
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setEmail(email);
        usuario.setTelefono(telefono);
        // Encripta la contraseña antes de guardarla para mayor seguridad
        usuario.setPassword(passwordEncoder.encode(password));
        // Asigna el rol "CLIENTE" al nuevo usuario
        usuario.setRoles(Set.of(rolCliente));

        // Guarda el nuevo usuario en la base de datos
        usuarioRepository.save(usuario);
        // Redirige al usuario a la página principal después de un registro exitoso
        return "redirect:/";
    }

}