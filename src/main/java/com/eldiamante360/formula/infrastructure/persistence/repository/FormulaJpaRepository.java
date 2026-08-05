package com.eldiamante360.formula.infrastructure.persistence.repository;

import com.eldiamante360.formula.infrastructure.persistence.entity.FormulaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FormulaJpaRepository extends JpaRepository<FormulaEntity, Long> {

    Optional<FormulaEntity> findByProductoId(Long productoId);

    boolean existsByProductoId(Long productoId);

    void deleteByProductoId(Long productoId);
}
