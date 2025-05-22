package com.proyecto.prestigio.repository;

import com.proyecto.prestigio.model.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServicioRepository extends JpaRepository<Servicio, Long> {
    List<Servicio> findByEstadoTrue();
    List<Servicio> findAll();

}

