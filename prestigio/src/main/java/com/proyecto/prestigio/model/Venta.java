package com.proyecto.prestigio.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Venta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Usuario usuario;

    private String direccionEnvio;
    private String tipoPago; // "EFECTIVO"
    private String estado; // "EN_PROCESO", "ENTREGADO", "CANCELADO"
    private LocalDateTime fecha;
    @Column(nullable = false)
    private Double total;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL)
    private List<VentaItem> items;
}
