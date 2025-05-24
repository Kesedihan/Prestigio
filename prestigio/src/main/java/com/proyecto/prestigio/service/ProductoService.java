package com.proyecto.prestigio.service;

import com.proyecto.prestigio.model.Producto;

import java.math.BigDecimal;
import java.util.List;

public interface ProductoService {
    List<Producto> listarActivos();
    List<Producto> filtrar(String tipo, BigDecimal min, BigDecimal max);
    Producto guardar(Producto producto);
    Producto obtenerPorId(Long id);
    void eliminar(Long id);
}

