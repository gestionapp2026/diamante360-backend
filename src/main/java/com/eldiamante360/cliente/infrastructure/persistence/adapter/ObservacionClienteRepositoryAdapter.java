package com.eldiamante360.cliente.infrastructure.persistence.adapter;

import com.eldiamante360.cliente.application.port.ObservacionClienteRepositoryPort;
import com.eldiamante360.cliente.domain.model.ObservacionCliente;
import com.eldiamante360.cliente.infrastructure.mapper.ObservacionClienteMapper;
import com.eldiamante360.cliente.infrastructure.persistence.entity.ObservacionClienteEntity;
import com.eldiamante360.cliente.infrastructure.persistence.repository.ObservacionClienteJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class ObservacionClienteRepositoryAdapter implements ObservacionClienteRepositoryPort {

    private final ObservacionClienteJpaRepository observacionClienteJpaRepository;
    private final ObservacionClienteMapper observacionClienteMapper;

    public ObservacionClienteRepositoryAdapter(ObservacionClienteJpaRepository observacionClienteJpaRepository,
                                                ObservacionClienteMapper observacionClienteMapper) {
        this.observacionClienteJpaRepository = observacionClienteJpaRepository;
        this.observacionClienteMapper = observacionClienteMapper;
    }

    @Override
    public ObservacionCliente guardar(ObservacionCliente observacion) {
        ObservacionClienteEntity entity = ObservacionClienteEntity.builder()
                .id(observacion.id())
                .clienteId(observacion.clienteId())
                .texto(observacion.texto())
                .usuarioId(observacion.usuarioId())
                .createdAt(observacion.fecha() != null ? observacion.fecha() : Instant.now())
                .build();

        ObservacionClienteEntity guardada = observacionClienteJpaRepository.save(entity);
        return observacionClienteMapper.toDomain(guardada);
    }

    @Override
    public Page<ObservacionCliente> listarPorCliente(Long clienteId, Pageable pageable) {
        return observacionClienteJpaRepository.findByClienteId(clienteId, pageable)
                .map(observacionClienteMapper::toDomain);
    }
}
