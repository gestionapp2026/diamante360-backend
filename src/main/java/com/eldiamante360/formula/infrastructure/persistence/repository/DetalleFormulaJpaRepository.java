package com.eldiamante360.formula.infrastructure.persistence.repository;

import com.eldiamante360.formula.infrastructure.persistence.entity.DetalleFormulaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repo minimo: los detalles de formula se gestionan siempre a traves del
 * agregado {@code FormulaEntity} (cascade), este repositorio solo existe
 * para poder validar si un insumo quimico esta referenciado en alguna
 * formula antes de permitir su eliminacion definitiva.
 */
public interface DetalleFormulaJpaRepository extends JpaRepository<DetalleFormulaEntity, Long> {

    boolean existsByInsumoId(Long insumoId);

    long countByInsumoId(Long insumoId);

    void deleteByInsumoId(Long insumoId);
}
