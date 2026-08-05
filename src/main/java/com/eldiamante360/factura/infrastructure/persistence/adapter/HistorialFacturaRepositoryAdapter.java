package com.eldiamante360.factura.infrastructure.persistence.adapter;

import com.eldiamante360.factura.application.port.HistorialFacturaRepositoryPort;
import com.eldiamante360.factura.domain.model.HistorialFactura;
import com.eldiamante360.factura.infrastructure.mapper.HistorialFacturaMapper;
import com.eldiamante360.factura.infrastructure.persistence.entity.HistorialFacturaEntity;
import com.eldiamante360.factura.infrastructure.persistence.repository.HistorialFacturaJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class HistorialFacturaRepositoryAdapter implements HistorialFacturaRepositoryPort {

    private final HistorialFacturaJpaRepository historialFacturaJpaRepository;
    private final HistorialFacturaMapper historialFacturaMapper;

    public HistorialFacturaRepositoryAdapter(HistorialFacturaJpaRepository historialFacturaJpaRepository,
                                              HistorialFacturaMapper historialFacturaMapper) {
        this.historialFacturaJpaRepository = historialFacturaJpaRepository;
        this.historialFacturaMapper = historialFacturaMapper;
    }

    @Override
    public HistorialFactura guardar(HistorialFactura historial) {
        HistorialFacturaEntity entity = HistorialFacturaEntity.builder()
                .id(historial.id())
                .facturaId(historial.facturaId())
                .tipoEvento(historial.tipoEvento())
                .descripcion(historial.descripcion())
                .usuarioId(historial.usuarioId())
                .createdAt(historial.fecha() != null ? historial.fecha() : Instant.now())
                .build();

        HistorialFacturaEntity guardado = historialFacturaJpaRepository.save(entity);
        return historialFacturaMapper.toDomain(guardado);
    }

    @Override
    public Page<HistorialFactura> listarPorFactura(Long facturaId, Pageable pageable) {
        return historialFacturaJpaRepository.findByFacturaId(facturaId, pageable).map(historialFacturaMapper::toDomain);
    }
}
