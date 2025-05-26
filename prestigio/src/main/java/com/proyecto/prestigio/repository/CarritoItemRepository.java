package com.proyecto.prestigio.repository;

import com.proyecto.prestigio.model.CarritoItem;
import com.proyecto.prestigio.model.Carrito;
import com.proyecto.prestigio.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarritoItemRepository extends JpaRepository<CarritoItem, Long> {
    List<CarritoItem> findByCarrito(Carrito carrito);
    void deleteByProducto(Producto producto);
    List<CarritoItem> findByProducto(Producto producto);
}
