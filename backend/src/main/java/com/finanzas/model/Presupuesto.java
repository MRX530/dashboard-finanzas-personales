package com.finanzas.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "presupuestos")
@Data
public class Presupuesto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private Double montoLimite;

    // Formato "2026-09": un presupuesto es un limite para todo el mes,
    // no tiene un dia especifico.
    @Column(nullable = false)
    private String mes;
}
