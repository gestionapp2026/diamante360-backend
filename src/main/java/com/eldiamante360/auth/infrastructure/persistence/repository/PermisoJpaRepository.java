package com.eldiamante360.auth.infrastructure.persistence.repository;

import com.eldiamante360.auth.infrastructure.persistence.entity.PermisoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermisoJpaRepository extends JpaRepository<PermisoEntity, Long> {
}
