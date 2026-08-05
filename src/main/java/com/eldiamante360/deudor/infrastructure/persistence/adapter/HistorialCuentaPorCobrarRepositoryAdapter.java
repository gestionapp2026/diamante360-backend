package com.eldiamante360.deudor.infrastructure.persistence.adapter;

import com.eldiamante360.deudor.application.port.HistorialCuentaPorCobrarRepositoryPort;
import com.eldiamante360.deudor.domain.model.HistorialCuentaPorCobrar;
import com.eldiamante360.deudor.infrastructure.mapper.HistorialCuentaPorCobrarMapper;
import com.eldiamante360.deudor.infrastructure.persistence.entity.HistorialCuentaPorCobrarEntity;
import com.eldiamante360.deudor.infrastructure.persistence.repository.HistorialCuentaPorCobrarJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class HistorialCuentaPorCobrarRepositoryAdapter implements HistorialCuentaPorCobrarRepositoryPort {

    private final HistorialCuentaPorCobrarJpaRepository historialCuentaPorCobrarJpaRepository;
    private final HistorialCuentaPorCobrarMapper historialCuentaPorCobrarMapper;

    public HistorialCuentaPorCobrarRepositoryAdapter(HistorialCuentaPorCobrarJpaRepository historialCuentaPorCobrarJpaRepository,
                                                       HistorialCuentaPorCobrarMapper historialCuentaPorCobrarMapper) {
        this.historialCuentaPorCobrarJpaRepository = historialCuentaPorCobrarJpaRepository;
        this.historialCuentaPorCobrarMapper = historialCuentaPorCobrarMapper;
    }

    @Override
    public HistorialCuentaPorCobrar guardar(HistorialCuentaPorCobrar historial) {
        HistorialCuentaPorCobrarEntity entity = HistorialCuentaPorCobrarEntity.builder()
                .id(historial.id())
                .cuentaPorCobrarId(historial.cuentaPorCobrarId())
                .tipoEvento(historial.tipoEvento())
                .descripcion(historial.descripcion())
                .usuarioId(historial.usuarioId())
                .createdAt(historial.fecha() != null ? historial.fecha() : Instant.now())
                .build();

        HistorialCuentaPorCobrarEntity guardado = historialCuentaPorCobrarJpaRepository.save(entity);
        return historialCuentaPorCobrarMapper.toDomain(guardado);
    }

    @Override
    public Page<HistorialCuentaPorCobrar> listarPorCuenta(Long cuentaPorCobrarId, Pageable pageable) {
        return historialCuentaPorCobrarJpaRepository.findByCuentaPorCobrarId(cuentaPorCobrarId, pageable)
                .map(historialCuentaPorCobrarMapper::toDomain);
    }
}
