package com.eldiamante360.deudor.infrastructure.persistence.entity;

import com.eldiamante360.deudor.domain.model.EstadoCuentaPorCobrar;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "cuenta_por_cobrar")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CuentaPorCobrarEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "factura_id", nullable = false, unique = true)
    private Long facturaId;

    @Column(name = "numero_factura", nullable = false, length = 20)
    private String numeroFactura;

    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;

    @Column(name = "cliente_nombre", nullable = false, length = 150)
    private String clienteNombre;

    @Column(name = "cliente_numero_documento", nullable = false, length = 20)
    private String clienteNumeroDocumento;

    @Column(name = "monto_original", nullable = false, precision = 14, scale = 2)
    private BigDecimal montoOriginal;

    @Column(name = "saldo_pendiente", nullable = false, precision = 14, scale = 2)
    private BigDecimal saldoPendiente;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoCuentaPorCobrar estado;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(nullable = false)
    private Instant fecha;

    @Column(name = "fecha_ultimo_abono")
    private Instant fechaUltimoAbono;

    @Column(name = "fecha_anulacion")
    private Instant fechaAnulacion;

    @Version
    private Integer version;
}
