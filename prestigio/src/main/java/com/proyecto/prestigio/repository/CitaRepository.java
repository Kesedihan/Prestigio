package com.proyecto.prestigio.repository;

import com.proyecto.prestigio.model.Cita;
import com.proyecto.prestigio.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CitaRepository extends JpaRepository<Cita, Long> {

    List<Cita> findAllByOrderByFechaAsc(); // ya lo usamos para admin
    List<Cita> findByUsuarioOrderByFechaDesc(Usuario usuario);
    List<Cita> findByEstado(String estado);
    List<Cita> findByUsuarioAndEstado(Usuario usuario, String estado);
    List<Cita> findByUsuarioAndEstadoOrderByFechaAsc(Usuario usuario, String estado);
    List<Cita> findByUsuario(Usuario usuario); // ← AGREGA ESTE
}


