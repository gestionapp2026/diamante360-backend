package com.eldiamante360.factura.infrastructure.persistence.entity;

import com.eldiamante360.factura.domain.model.EstadoFactura;
import com.eldiamante360.factura.domain.model.TipoPago;
import com.eldiamante360.shared.domain.model.MedioPago;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "factura")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacturaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String numero;

    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;

    @Column(name = "cliente_nombre", nullable = false, length = 150)
    private String clienteNombre;

    @Column(name = "cliente_numero_documento", nullable = false, length = 20)
    private String clienteNumeroDocumento;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_pago", nullable = false, length = 10)
    private TipoPago tipoPago;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoFactura estado;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal subtotal;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal descuento;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal total;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "usuario_nombre", nullable = false, length = 150)
    private String usuarioNombre;

    @Column(nullable = false)
    private Instant fecha;

    @Column(name = "fecha_anulacion")
    private Instant fechaAnulacion;

    @Version
    private Integer version;

    @Enumerated(EnumType.STRING)
    @Column(name = "medio_pago", length = 20)
    private MedioPago medioPago;

    @Builder.Default
    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<DetalleFacturaEntity> detalles = new ArrayList<>();

    public void agregarDetalle(DetalleFacturaEntity detalle) {
        detalle.setFactura(this);
        this.detalles.add(detalle);
    }
}
