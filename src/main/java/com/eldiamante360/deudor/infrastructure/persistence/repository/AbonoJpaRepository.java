package com.eldiamante360.deudor.infrastructure.persistence.repository;

import com.eldiamante360.deudor.infrastructure.persistence.entity.AbonoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AbonoJpaRepository extends JpaRepository<AbonoEntity, Long> {

    Page<AbonoEntity> findByCuentaPorCobrarId(Long cuentaPorCobrarId, Pageable pageable);

    boolean existsByUsuarioId(Long usuarioId);

    long countByUsuarioId(Long usuarioId);

    void deleteByUsuarioId(Long usuarioId);
}
