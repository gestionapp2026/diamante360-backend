package com.eldiamante360.inventario.infrastructure.persistence.adapter;

import com.eldiamante360.inventario.application.port.MovimientoInventarioRepositoryPort;
import com.eldiamante360.inventario.domain.model.MovimientoInventario;
import com.eldiamante360.inventario.infrastructure.mapper.MovimientoInventarioMapper;
import com.eldiamante360.inventario.infrastructure.persistence.entity.MovimientoInventarioEntity;
import com.eldiamante360.inventario.infrastructure.persistence.repository.MovimientoInventarioJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class MovimientoInventarioRepositoryAdapter implements MovimientoInventarioRepositoryPort {

    private final MovimientoInventarioJpaRepository movimientoInventarioJpaRepository;
    private final MovimientoInventarioMapper movimientoInventarioMapper;

    public MovimientoInventarioRepositoryAdapter(MovimientoInventarioJpaRepository movimientoInventarioJpaRepository,
                                                   MovimientoInventarioMapper movimientoInventarioMapper) {
        this.movimientoInventarioJpaRepository = movimientoInventarioJpaRepository;
        this.movimientoInventarioMapper = movimientoInventarioMapper;
    }

    @Override
    public MovimientoInventario guardar(MovimientoInventario movimiento) {
        MovimientoInventarioEntity entity = MovimientoInventarioEntity.builder()
                .id(movimiento.id())
                .productoId(movimiento.productoId())
                .tipoMovimiento(movimiento.tipoMovimiento())
                .cantidad(movimiento.cantidad())
                .stockResultante(movimiento.stockResultante())
                .motivo(movimiento.motivo())
                .usuarioId(movimiento.usuarioId())
                .fecha(movimiento.fecha() != null ? movimiento.fecha() : Instant.now())
                .build();

        MovimientoInventarioEntity guardado = movimientoInventarioJpaRepository.save(entity);
        return movimientoInventarioMapper.toDomain(guardado);
    }

    @Override
    public Page<MovimientoInventario> listarPorProducto(Long productoId, Pageable pageable) {
        return movimientoInventarioJpaRepository.findByProductoId(productoId, pageable)
                .map(movimientoInventarioMapper::toDomain);
    }
}
