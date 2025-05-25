package com.proyecto.prestigio.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private String descripcion;

    private BigDecimal precio;

    private Integer stock;

    private String tipo; // Ej: "Cosmético", "Medicamento", "Accesorio", etc.

    private Boolean activo = true;

    private String imagen;

}


