package com.eldiamante360.insumoquimico.infrastructure.persistence.repository;

import com.eldiamante360.insumoquimico.infrastructure.persistence.entity.InsumoQuimicoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InsumoQuimicoJpaRepository extends JpaRepository<InsumoQuimicoEntity, Long> {

    boolean existsByNombre(String nombre);
}
