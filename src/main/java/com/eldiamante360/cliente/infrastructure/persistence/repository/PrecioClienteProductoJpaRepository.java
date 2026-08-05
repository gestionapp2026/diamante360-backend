package com.eldiamante360.cliente.infrastructure.persistence.repository;

import com.eldiamante360.cliente.infrastructure.persistence.entity.PrecioClienteProductoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PrecioClienteProductoJpaRepository extends JpaRepository<PrecioClienteProductoEntity, Long> {

    Optional<PrecioClienteProductoEntity> findByClienteIdAndProductoId(Long clienteId, Long productoId);

    Page<PrecioClienteProductoEntity> findByClienteId(Long clienteId, Pageable pageable);
}
