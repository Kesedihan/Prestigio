package com.proyecto.prestigio.service;


import com.proyecto.prestigio.model.Rol;
import java.util.List;
import java.util.Optional;

public interface RolService {
    Rol crearRol(Rol rol);
    Optional<Rol> obtenerPorNombre(String nombre);
    List<Rol> obtenerTodos();
}


