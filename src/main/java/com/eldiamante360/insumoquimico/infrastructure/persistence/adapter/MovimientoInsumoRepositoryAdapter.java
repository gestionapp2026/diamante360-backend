package com.eldiamante360.insumoquimico.infrastructure.persistence.adapter;

import com.eldiamante360.insumoquimico.application.port.MovimientoInsumoRepositoryPort;
import com.eldiamante360.insumoquimico.domain.model.MovimientoInsumo;
import com.eldiamante360.insumoquimico.infrastructure.mapper.MovimientoInsumoMapper;
import com.eldiamante360.insumoquimico.infrastructure.persistence.entity.MovimientoInsumoEntity;
import com.eldiamante360.insumoquimico.infrastructure.persistence.repository.MovimientoInsumoJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class MovimientoInsumoRepositoryAdapter implements MovimientoInsumoRepositoryPort {

    private final MovimientoInsumoJpaRepository movimientoInsumoJpaRepository;
    private final MovimientoInsumoMapper movimientoInsumoMapper;

    public MovimientoInsumoRepositoryAdapter(MovimientoInsumoJpaRepository movimientoInsumoJpaRepository,
                                              MovimientoInsumoMapper movimientoInsumoMapper) {
        this.movimientoInsumoJpaRepository = movimientoInsumoJpaRepository;
        this.movimientoInsumoMapper = movimientoInsumoMapper;
    }

    @Override
    public MovimientoInsumo guardar(MovimientoInsumo movimiento) {
        MovimientoInsumoEntity entity = MovimientoInsumoEntity.builder()
                .id(movimiento.id())
                .insumoId(movimiento.insumoId())
                .loteId(movimiento.loteId())
                .tipoMovimiento(movimiento.tipoMovimiento())
                .cantidad(movimiento.cantidad())
                .stockResultante(movimiento.stockResultante())
                .motivo(movimiento.motivo())
                .usuarioId(movimiento.usuarioId())
                .fecha(movimiento.fecha() != null ? movimiento.fecha() : Instant.now())
                .build();

        MovimientoInsumoEntity guardado = movimientoInsumoJpaRepository.save(entity);
        return movimientoInsumoMapper.toDomain(guardado);
    }

    @Override
    public Page<MovimientoInsumo> listarPorInsumo(Long insumoId, Pageable pageable) {
        return movimientoInsumoJpaRepository.findByInsumoId(insumoId, pageable)
                .map(movimientoInsumoMapper::toDomain);
    }
}
