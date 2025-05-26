package com.proyecto.prestigio.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CarritoItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Carrito carrito;

    @ManyToOne
    @JoinColumn(name = "producto_id", foreignKey = @ForeignKey(name = "fk_carrito_item_producto"))
    private Producto producto;

    private int cantidad;
}
