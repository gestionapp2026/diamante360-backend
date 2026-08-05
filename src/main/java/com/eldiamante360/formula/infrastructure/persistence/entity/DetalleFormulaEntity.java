package com.eldiamante360.formula.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "detalle_formula")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleFormulaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "formula_id", nullable = false)
    private FormulaEntity formula;

    @Column(name = "insumo_id", nullable = false)
    private Long insumoId;

    @Column(nullable = false)
    private Integer numero;

    @Column(nullable = false, precision = 14, scale = 5)
    private BigDecimal cantidad;
}
