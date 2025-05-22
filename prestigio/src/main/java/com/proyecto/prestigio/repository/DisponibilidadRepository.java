package com.proyecto.prestigio.repository;

import com.proyecto.prestigio.model.Disponibilidad;
import com.proyecto.prestigio.model.Servicio; // Asegúrate de que esta importación exista
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DisponibilidadRepository extends JpaRepository<Disponibilidad, Long> {
    List<Disponibilidad> findByDisponibleTrue();
    List<Disponibilidad> findByDisponibleTrueAndServicioId(Long servicioId);

}



