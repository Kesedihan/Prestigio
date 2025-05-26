package com.proyecto.prestigio.controller;

import com.proyecto.prestigio.model.Usuario;
import com.proyecto.prestigio.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de usuarios en la aplicación Prestigio.
 * Expone endpoints para la creación y consulta de usuarios.
 */
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    /**
     * Constructor para inyectar la dependencia de UsuarioService.
     * @param usuarioService El servicio que gestiona la lógica de negocio de los usuarios.
     */
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Crea un nuevo usuario en el sistema.
     * Este endpoint recibe un objeto Usuario en el cuerpo de la solicitud y lo guarda a través del servicio.
     *
     * @param usuario El objeto Usuario a ser creado. Se espera que contenga los datos necesarios del usuario.
     * @return ResponseEntity con el usuario creado y un estado HTTP 200 OK si la operación es exitosa.
     * Puede retornar otros códigos de estado en caso de errores (ej. 400 Bad Request si el objeto es inválido).
     */
    @PostMapping
    public ResponseEntity<Usuario> crearUsuario(@RequestBody Usuario usuario) {
        return ResponseEntity.ok(usuarioService.crearUsuario(usuario));
    }

    /**
     * Obtiene una lista de todos los usuarios registrados en el sistema.
     *
     * @return ResponseEntity con una lista de objetos Usuario y un estado HTTP 200 OK.
     * La lista puede estar vacía si no hay usuarios registrados.
     */
    @GetMapping
    public ResponseEntity<List<Usuario>> obtenerUsuarios() {
        return ResponseEntity.ok(usuarioService.obtenerTodos());
    }
}
