package com.eldiamante360.cliente.infrastructure.persistence.repository;

import com.eldiamante360.cliente.infrastructure.persistence.entity.RutaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RutaJpaRepository extends JpaRepository<RutaEntity, Long> {

    boolean existsByNombre(String nombre);
}
