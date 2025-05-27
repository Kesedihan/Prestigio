package com.proyecto.prestigio.repository;

import com.proyecto.prestigio.model.Venta;
import com.proyecto.prestigio.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VentaRepository extends JpaRepository<Venta, Long> {
    List<Venta> findByUsuarioOrderByFechaDesc(Usuario usuario);
}