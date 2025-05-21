package com.proyecto.prestigio.service;


import com.proyecto.prestigio.model.Usuario;
import java.util.List;
import java.util.Optional;

public interface UsuarioService {
    Usuario crearUsuario(Usuario usuario);
    Optional<Usuario> obtenerPorId(Long id);
    List<Usuario> obtenerTodos();
}


