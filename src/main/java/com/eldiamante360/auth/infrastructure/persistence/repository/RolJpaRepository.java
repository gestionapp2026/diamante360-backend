package com.eldiamante360.auth.infrastructure.persistence.repository;

import com.eldiamante360.auth.infrastructure.persistence.entity.RolEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolJpaRepository extends JpaRepository<RolEntity, Long> {

    Optional<RolEntity> findByNombre(String nombre);
}
