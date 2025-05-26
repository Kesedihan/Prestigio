package com.proyecto.prestigio.controller;

import com.proyecto.prestigio.model.Rol;
import com.proyecto.prestigio.service.RolService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de roles en la aplicación Prestigio.
 * Expone endpoints para la creación y consulta de roles.
 */
@RestController
@RequestMapping("/api/roles")
public class RolController {

    private final RolService rolService;

    /**
     * Constructor para inyectar la dependencia de RolService.
     * @param rolService El servicio que gestiona la lógica de negocio de los roles.
     */
    public RolController(RolService rolService) {
        this.rolService = rolService;
    }

    /**
     * Crea un nuevo rol en el sistema.
     * Este endpoint recibe un objeto Rol en el cuerpo de la solicitud y lo guarda a través del servicio.
     *
     * @param rol El objeto Rol a ser creado. Se espera que contenga los datos necesarios del rol.
     * @return ResponseEntity con el rol creado y un estado HTTP 200 OK si la operación es exitosa.
     * Puede retornar otros códigos de estado en caso de errores (ej. 400 Bad Request si el objeto es inválido).
     */
    @PostMapping
    public ResponseEntity<Rol> crearRol(@RequestBody Rol rol) {
        return ResponseEntity.ok(rolService.crearRol(rol));
    }

    /**
     * Obtiene una lista de todos los roles registrados en el sistema.
     *
     * @return ResponseEntity con una lista de objetos Rol y un estado HTTP 200 OK.
     * La lista puede estar vacía si no hay roles registrados.
     */
    @GetMapping
    public ResponseEntity<List<Rol>> obtenerTodos() {
        return ResponseEntity.ok(rolService.obtenerTodos());
    }
}