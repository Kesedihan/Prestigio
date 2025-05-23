package com.proyecto.prestigio.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Deuda {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombrePersona;
    private String descripcion;
    private BigDecimal montoTotal;

    @OneToMany(mappedBy = "deuda", cascade = CascadeType.ALL)
    private List<Abono> abonos = new ArrayList<>();

    public BigDecimal getMontoAbonado() {
        return abonos.stream()
                .map(Abono::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getSaldoPendiente() {
        return montoTotal.subtract(getMontoAbonado());
    }

}

