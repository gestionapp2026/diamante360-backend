package com.eldiamante360.deudor.infrastructure.persistence.repository;

import com.eldiamante360.deudor.infrastructure.persistence.entity.HistorialCuentaPorCobrarEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistorialCuentaPorCobrarJpaRepository extends JpaRepository<HistorialCuentaPorCobrarEntity, Long> {

    Page<HistorialCuentaPorCobrarEntity> findByCuentaPorCobrarId(Long cuentaPorCobrarId, Pageable pageable);

    boolean existsByUsuarioId(Long usuarioId);

    long countByUsuarioId(Long usuarioId);

    void deleteByUsuarioId(Long usuarioId);
}
