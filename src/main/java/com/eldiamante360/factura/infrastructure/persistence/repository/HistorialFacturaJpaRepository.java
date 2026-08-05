package com.eldiamante360.factura.infrastructure.persistence.repository;

import com.eldiamante360.factura.infrastructure.persistence.entity.HistorialFacturaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistorialFacturaJpaRepository extends JpaRepository<HistorialFacturaEntity, Long> {

    Page<HistorialFacturaEntity> findByFacturaId(Long facturaId, Pageable pageable);

    boolean existsByUsuarioId(Long usuarioId);

    long countByUsuarioId(Long usuarioId);

    void deleteByUsuarioId(Long usuarioId);
}
