package com.eldiamante360.factura.infrastructure.persistence.entity;

import com.eldiamante360.factura.domain.model.TipoEventoFactura;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "historial_factura")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistorialFacturaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "factura_id", nullable = false)
    private Long facturaId;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_evento", nullable = false, length = 30)
    private TipoEventoFactura tipoEvento;

    @Column(nullable = false, length = 300)
    private String descripcion;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
