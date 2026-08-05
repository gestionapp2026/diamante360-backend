package com.eldiamante360.orden.infrastructure.persistence.entity;

import com.eldiamante360.orden.domain.model.EstadoOrden;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orden")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String numero;

    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;

    @Column(name = "cliente_nombre", nullable = false, length = 150)
    private String clienteNombre;

    @Column(name = "fecha_creacion", nullable = false)
    private Instant fechaCreacion;

    @Column(name = "fecha_entrega", nullable = false)
    private LocalDate fechaEntrega;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoOrden estado;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "usuario_nombre", nullable = false, length = 150)
    private String usuarioNombre;

    @Column(name = "fecha_despacho")
    private Instant fechaDespacho;

    @Column(name = "fecha_anulacion")
    private Instant fechaAnulacion;

    @Version
    private Integer version;

    @Builder.Default
    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<DetalleOrdenEntity> detalles = new ArrayList<>();

    public void agregarDetalle(DetalleOrdenEntity detalle) {
        detalle.setOrden(this);
        this.detalles.add(detalle);
    }
}
