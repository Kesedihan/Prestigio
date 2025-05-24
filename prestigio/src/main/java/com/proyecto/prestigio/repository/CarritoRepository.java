package com.proyecto.prestigio.repository;

import com.proyecto.prestigio.model.Carrito;
import com.proyecto.prestigio.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarritoRepository extends JpaRepository<Carrito, Long> {
    Optional<Carrito> findByUsuario(Usuario usuario);
}