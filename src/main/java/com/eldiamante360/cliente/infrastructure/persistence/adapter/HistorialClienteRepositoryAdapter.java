package com.eldiamante360.cliente.infrastructure.persistence.adapter;

import com.eldiamante360.cliente.application.port.HistorialClienteRepositoryPort;
import com.eldiamante360.cliente.domain.model.HistorialCliente;
import com.eldiamante360.cliente.infrastructure.mapper.HistorialClienteMapper;
import com.eldiamante360.cliente.infrastructure.persistence.entity.HistorialClienteEntity;
import com.eldiamante360.cliente.infrastructure.persistence.repository.HistorialClienteJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class HistorialClienteRepositoryAdapter implements HistorialClienteRepositoryPort {

    private final HistorialClienteJpaRepository historialClienteJpaRepository;
    private final HistorialClienteMapper historialClienteMapper;

    public HistorialClienteRepositoryAdapter(HistorialClienteJpaRepository historialClienteJpaRepository,
                                              HistorialClienteMapper historialClienteMapper) {
        this.historialClienteJpaRepository = historialClienteJpaRepository;
        this.historialClienteMapper = historialClienteMapper;
    }

    @Override
    public HistorialCliente guardar(HistorialCliente historial) {
        HistorialClienteEntity entity = HistorialClienteEntity.builder()
                .id(historial.id())
                .clienteId(historial.clienteId())
                .tipoEvento(historial.tipoEvento())
                .descripcion(historial.descripcion())
                .usuarioId(historial.usuarioId())
                .createdAt(historial.fecha() != null ? historial.fecha() : Instant.now())
                .build();

        HistorialClienteEntity guardado = historialClienteJpaRepository.save(entity);
        return historialClienteMapper.toDomain(guardado);
    }

    @Override
    public Page<HistorialCliente> listarPorCliente(Long clienteId, Pageable pageable) {
        return historialClienteJpaRepository.findByClienteId(clienteId, pageable)
                .map(historialClienteMapper::toDomain);
    }
}
