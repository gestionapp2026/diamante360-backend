package com.eldiamante360.deudor.infrastructure.persistence.entity;

import com.eldiamante360.deudor.domain.model.TipoEventoCuentaPorCobrar;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "historial_cuenta_por_cobrar")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistorialCuentaPorCobrarEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cuenta_por_cobrar_id", nullable = false)
    private Long cuentaPorCobrarId;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_evento", nullable = false, length = 30)
    private TipoEventoCuentaPorCobrar tipoEvento;

    @Column(nullable = false, length = 300)
    private String descripcion;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
