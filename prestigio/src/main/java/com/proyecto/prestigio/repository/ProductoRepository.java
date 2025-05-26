package com.proyecto.prestigio.repository;

import com.proyecto.prestigio.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByActivoTrue();
    List<Producto> findByActivoTrueAndTipo(String tipo);
    List<Producto> findByActivoTrueAndPrecioBetween(BigDecimal min, BigDecimal max);
    List<Producto> findByActivoTrueAndTipoAndPrecioBetween(String tipo, BigDecimal min, BigDecimal max);

}

