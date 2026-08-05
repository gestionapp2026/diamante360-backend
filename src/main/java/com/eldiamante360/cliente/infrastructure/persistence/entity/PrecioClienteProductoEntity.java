package com.eldiamante360.cliente.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "precio_cliente_producto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrecioClienteProductoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;

    @Column(name = "producto_id", nullable = false)
    private Long productoId;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal precio;

    @Version
    private Integer version;
}
