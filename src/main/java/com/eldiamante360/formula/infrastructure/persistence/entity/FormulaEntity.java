package com.eldiamante360.formula.infrastructure.persistence.entity;

import com.eldiamante360.insumoquimico.domain.model.UnidadMedidaInsumo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "formula")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormulaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "producto_id", nullable = false, unique = true)
    private Long productoId;

    @Column(name = "producto_nombre", nullable = false, length = 120)
    private String productoNombre;

    @Column(name = "cantidad_base", nullable = false, precision = 12, scale = 3)
    private BigDecimal cantidadBase;

    @Enumerated(EnumType.STRING)
    @Column(name = "unidad_base", nullable = false, length = 10)
    private UnidadMedidaInsumo unidadBase;

    @Column(nullable = false)
    private boolean activo;

    @Version
    private Integer version;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @CreatedBy
    @Column(name = "created_by", updatable = false, length = 50)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    @Builder.Default
    @OneToMany(mappedBy = "formula", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<DetalleFormulaEntity> detalles = new ArrayList<>();

    public void agregarDetalle(DetalleFormulaEntity detalle) {
        detalle.setFormula(this);
        this.detalles.add(detalle);
    }
}
