package com.eldiamante360.insumoquimico.infrastructure.persistence.entity;

import com.eldiamante360.insumoquimico.domain.model.TipoMovimientoInsumo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "movimiento_insumo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimientoInsumoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "insumo_id", nullable = false)
    private Long insumoId;

    @Column(name = "lote_id", nullable = false)
    private Long loteId;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimiento", nullable = false, length = 20)
    private TipoMovimientoInsumo tipoMovimiento;

    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal cantidad;

    @Column(name = "stock_resultante", nullable = false, precision = 12, scale = 3)
    private BigDecimal stockResultante;

    @Column(nullable = false, length = 200)
    private String motivo;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant fecha;
}
