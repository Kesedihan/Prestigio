package com.proyecto.prestigio.repository;

import com.proyecto.prestigio.model.Cita;
import com.proyecto.prestigio.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CitaRepository extends JpaRepository<Cita, Long> {
    List<Cita> findByUsuario(Usuario usuario);
}

