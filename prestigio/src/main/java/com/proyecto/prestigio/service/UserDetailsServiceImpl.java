package com.proyecto.prestigio.service;

import com.proyecto.prestigio.model.Usuario;
import com.proyecto.prestigio.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.*; // Interfaz UserDetailsService y clases relacionadas
import org.springframework.security.core.authority.SimpleGrantedAuthority; // Para manejar los roles/autoridades
import org.springframework.stereotype.Service;

import java.util.stream.Collectors; // Para operar con Streams y recolectar en colecciones

/**
 * Servicio que implementa la interfaz {@link UserDetailsService} de Spring Security.
 * Esta clase es crucial para el proceso de autenticación de usuarios, ya que se encarga
 * de cargar los detalles de un usuario a partir de su nombre de usuario (en este caso, el email)
 * desde la base de datos.
 * Spring Security utiliza esta implementación para verificar las credenciales del usuario durante el login.
 * Utiliza {@code @Service} para ser reconocido como un componente de servicio de Spring.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    /**
     * Constructor para inyectar la dependencia del repositorio de usuarios.
     * Spring se encarga de proporcionar la instancia de {@link UsuarioRepository}.
     * Este repositorio es necesario para buscar al usuario en la base de datos.
     *
     * @param usuarioRepository Repositorio para acceder y gestionar los datos de los usuarios.
     */
    public UserDetailsServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Implementación del método central de {@link UserDetailsService}.
     * Este método es invocado por Spring Security durante el proceso de autenticación
     * para obtener los detalles de un usuario basándose en su nombre de usuario (email).
     *
     * @param email El nombre de usuario (email) proporcionado durante el intento de login.
     * @return Un objeto {@link UserDetails} que contiene la información del usuario (email, contraseña, autoridades/roles).
     * @throws UsernameNotFoundException Si no se encuentra un usuario con el email proporcionado en la base de datos.
     * @Override indica que este método implementa un método de la interfaz {@link UserDetailsService}.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Busca el usuario en la base de datos por su email.
        // Si el usuario no se encuentra, lanza una UsernameNotFoundException,
        // que Spring Security capturará y manejará (ej. mostrando un mensaje de error de credenciales).
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));

        // Construye y retorna un objeto org.springframework.security.core.userdetails.User.
        // Este objeto es una implementación de la interfaz UserDetails y contiene:
        // 1. El email del usuario (username para Spring Security).
        // 2. La contraseña del usuario (se asume que está cifrada si se usa un PasswordEncoder).
        // 3. Las autoridades (roles) del usuario, convertidas a SimpleGrantedAuthority.
        //    Se agrega el prefijo "ROLE_" a cada rol, que es una convención común en Spring Security
        //    para el manejo de roles (ej. "ADMIN" se convierte en "ROLE_ADMIN").
        return new User(
                usuario.getEmail(),                                 // Username
                usuario.getPassword(),                              // Password (ya debería estar cifrado)
                usuario.getRoles().stream()                         // Convierte los roles del modelo a autoridades de Spring Security
                        .map(rol -> new SimpleGrantedAuthority("ROLE_" + rol.getNombre()))
                        .collect(Collectors.toSet())                // Recolecta las autoridades en un Set
        );
    }
}