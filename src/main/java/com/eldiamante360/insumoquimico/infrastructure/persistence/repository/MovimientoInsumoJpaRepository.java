package com.eldiamante360.insumoquimico.infrastructure.persistence.repository;

import com.eldiamante360.insumoquimico.infrastructure.persistence.entity.MovimientoInsumoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovimientoInsumoJpaRepository extends JpaRepository<MovimientoInsumoEntity, Long> {

    Page<MovimientoInsumoEntity> findByInsumoId(Long insumoId, Pageable pageable);

    boolean existsByInsumoId(Long insumoId);

    boolean existsByUsuarioId(Long usuarioId);

    long countByInsumoId(Long insumoId);

    long countByUsuarioId(Long usuarioId);

    void deleteByInsumoId(Long insumoId);

    void deleteByUsuarioId(Long usuarioId);
}
