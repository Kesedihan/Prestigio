package com.proyecto.prestigio.repository;

import com.proyecto.prestigio.model.VentaItem;
import com.proyecto.prestigio.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VentaItemRepository extends JpaRepository<VentaItem, Long> {
    List<VentaItem> findByVenta(Venta venta);

}
