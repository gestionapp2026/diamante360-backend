package com.eldiamante360.insumoquimico.infrastructure.persistence.repository;

import com.eldiamante360.insumoquimico.infrastructure.persistence.entity.LoteInsumoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface LoteInsumoJpaRepository extends JpaRepository<LoteInsumoEntity, Long> {

    Page<LoteInsumoEntity> findByInsumoId(Long insumoId, Pageable pageable);

    @Query("SELECT l FROM LoteInsumoEntity l WHERE l.fechaVencimiento IS NOT NULL AND l.fechaVencimiento <= :hasta "
            + "AND l.cantidadActual > 0 ORDER BY l.fechaVencimiento ASC")
    List<LoteInsumoEntity> findPorVencer(@Param("hasta") LocalDate hasta);

    @Query("SELECT l FROM LoteInsumoEntity l WHERE l.insumoId = :insumoId AND l.cantidadActual > 0 "
            + "ORDER BY l.fechaVencimiento ASC, l.fechaIngreso ASC")
    List<LoteInsumoEntity> findDisponiblesFefo(@Param("insumoId") Long insumoId);
}
