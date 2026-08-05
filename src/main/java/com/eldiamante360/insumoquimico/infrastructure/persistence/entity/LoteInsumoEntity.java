package com.eldiamante360.insumoquimico.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "lote_insumo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoteInsumoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "insumo_id", nullable = false)
    private Long insumoId;

    @Column(name = "numero_lote", length = 60)
    private String numeroLote;

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;

    @Column(name = "cantidad_actual", nullable = false, precision = 12, scale = 3)
    private BigDecimal cantidadActual;

    @Column(name = "fecha_ingreso", nullable = false)
    private Instant fechaIngreso;

    @Version
    private Integer version;
}
