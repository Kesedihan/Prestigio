package com.proyecto.prestigio.repository;

import com.proyecto.prestigio.model.CarritoItem;
import com.proyecto.prestigio.model.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarritoItemRepository extends JpaRepository<CarritoItem, Long> {
    List<CarritoItem> findByCarrito(Carrito carrito);
}
