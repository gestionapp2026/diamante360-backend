package com.eldiamante360.inventario.infrastructure.persistence.repository;

import com.eldiamante360.inventario.infrastructure.persistence.entity.MovimientoInventarioEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovimientoInventarioJpaRepository extends JpaRepository<MovimientoInventarioEntity, Long> {

    Page<MovimientoInventarioEntity> findByProductoId(Long productoId, Pageable pageable);

    boolean existsByProductoId(Long productoId);

    boolean existsByUsuarioId(Long usuarioId);

    long countByProductoId(Long productoId);

    long countByUsuarioId(Long usuarioId);

    void deleteByProductoId(Long productoId);

    void deleteByUsuarioId(Long usuarioId);
}
