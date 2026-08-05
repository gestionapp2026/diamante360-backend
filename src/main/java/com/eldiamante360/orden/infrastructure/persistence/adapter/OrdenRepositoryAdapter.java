package com.eldiamante360.orden.infrastructure.persistence.adapter;

import com.eldiamante360.orden.application.dto.OrdenFiltro;
import com.eldiamante360.orden.application.port.OrdenRepositoryPort;
import com.eldiamante360.orden.domain.model.DetalleOrden;
import com.eldiamante360.orden.domain.model.Orden;
import com.eldiamante360.orden.infrastructure.mapper.OrdenMapper;
import com.eldiamante360.orden.infrastructure.persistence.entity.DetalleOrdenEntity;
import com.eldiamante360.orden.infrastructure.persistence.entity.OrdenEntity;
import com.eldiamante360.orden.infrastructure.persistence.repository.OrdenJpaRepository;
import com.eldiamante360.orden.infrastructure.persistence.specification.OrdenSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.Year;
import java.util.Optional;

@Component
public class OrdenRepositoryAdapter implements OrdenRepositoryPort {

    private final OrdenJpaRepository ordenJpaRepository;
    private final OrdenMapper ordenMapper;

    public OrdenRepositoryAdapter(OrdenJpaRepository ordenJpaRepository, OrdenMapper ordenMapper) {
        this.ordenJpaRepository = ordenJpaRepository;
        this.ordenMapper = ordenMapper;
    }

    @Override
    public Optional<Orden> buscarPorId(Long id) {
        return ordenJpaRepository.findById(id).map(ordenMapper::toDomain);
    }

    @Override
    public Orden guardar(Orden orden) {
        OrdenEntity entity = OrdenEntity.builder()
                .id(orden.getId())
                .numero(orden.getNumero())
                .clienteId(orden.getClienteId())
                .clienteNombre(orden.getClienteNombre())
                .fechaCreacion(orden.getFechaCreacion())
                .fechaEntrega(orden.getFechaEntrega())
                .estado(orden.getEstado())
                .observaciones(orden.getObservaciones())
                .usuarioId(orden.getUsuarioId())
                .usuarioNombre(orden.getUsuarioNombre())
                .fechaDespacho(orden.getFechaDespacho())
                .fechaAnulacion(orden.getFechaAnulacion())
                .version(orden.getVersion())
                .build();

        for (DetalleOrden detalle : orden.getDetalles()) {
            entity.agregarDetalle(DetalleOrdenEntity.builder()
                    .id(detalle.id())
                    .productoId(detalle.productoId())
                    .productoNombre(detalle.productoNombre())
                    .cantidad(detalle.cantidad())
                    .build());
        }

        OrdenEntity guardado = ordenJpaRepository.save(entity);
        return ordenMapper.toDomain(guardado);
    }

    @Override
    public Page<Orden> listar(OrdenFiltro filtro, Pageable pageable) {
        return ordenJpaRepository.findAll(OrdenSpecifications.conFiltro(filtro), pageable).map(ordenMapper::toDomain);
    }

    @Override
    public String siguienteNumero() {
        Long secuencia = ordenJpaRepository.siguienteNumeroSecuencia();
        return "OR-%d-%05d".formatted(Year.now().getValue(), secuencia);
    }

    @Override
    public void eliminar(Long id) {
        ordenJpaRepository.deleteById(id);
    }
}
