package com.eldiamante360.cliente.infrastructure.persistence.repository;

import com.eldiamante360.cliente.infrastructure.persistence.entity.ObservacionClienteEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ObservacionClienteJpaRepository extends JpaRepository<ObservacionClienteEntity, Long> {

    Page<ObservacionClienteEntity> findByClienteId(Long clienteId, Pageable pageable);

    boolean existsByUsuarioId(Long usuarioId);

    long countByUsuarioId(Long usuarioId);

    void deleteByUsuarioId(Long usuarioId);
}
