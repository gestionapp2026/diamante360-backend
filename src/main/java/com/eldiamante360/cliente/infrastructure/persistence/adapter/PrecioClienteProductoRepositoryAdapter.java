package com.eldiamante360.cliente.infrastructure.persistence.adapter;

import com.eldiamante360.cliente.application.port.PrecioClienteProductoRepositoryPort;
import com.eldiamante360.cliente.domain.model.PrecioClienteProducto;
import com.eldiamante360.cliente.infrastructure.mapper.PrecioClienteProductoMapper;
import com.eldiamante360.cliente.infrastructure.persistence.entity.PrecioClienteProductoEntity;
import com.eldiamante360.cliente.infrastructure.persistence.repository.PrecioClienteProductoJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PrecioClienteProductoRepositoryAdapter implements PrecioClienteProductoRepositoryPort {

    private final PrecioClienteProductoJpaRepository precioClienteProductoJpaRepository;
    private final PrecioClienteProductoMapper precioClienteProductoMapper;

    public PrecioClienteProductoRepositoryAdapter(PrecioClienteProductoJpaRepository precioClienteProductoJpaRepository,
                                                    PrecioClienteProductoMapper precioClienteProductoMapper) {
        this.precioClienteProductoJpaRepository = precioClienteProductoJpaRepository;
        this.precioClienteProductoMapper = precioClienteProductoMapper;
    }

    @Override
    public PrecioClienteProducto guardar(PrecioClienteProducto precio) {
        PrecioClienteProductoEntity entity = PrecioClienteProductoEntity.builder()
                .id(precio.getId())
                .clienteId(precio.getClienteId())
                .productoId(precio.getProductoId())
                .precio(precio.getPrecio())
                .version(precio.getVersion())
                .build();

        PrecioClienteProductoEntity guardado = precioClienteProductoJpaRepository.save(entity);
        return precioClienteProductoMapper.toDomain(guardado);
    }

    @Override
    public Optional<PrecioClienteProducto> buscarPorId(Long id) {
        return precioClienteProductoJpaRepository.findById(id).map(precioClienteProductoMapper::toDomain);
    }

    @Override
    public Optional<PrecioClienteProducto> buscarPorClienteYProducto(Long clienteId, Long productoId) {
        return precioClienteProductoJpaRepository.findByClienteIdAndProductoId(clienteId, productoId)
                .map(precioClienteProductoMapper::toDomain);
    }

    @Override
    public Page<PrecioClienteProducto> listarPorCliente(Long clienteId, Pageable pageable) {
        return precioClienteProductoJpaRepository.findByClienteId(clienteId, pageable)
                .map(precioClienteProductoMapper::toDomain);
    }

    @Override
    public void eliminar(Long id) {
        precioClienteProductoJpaRepository.deleteById(id);
    }
}
