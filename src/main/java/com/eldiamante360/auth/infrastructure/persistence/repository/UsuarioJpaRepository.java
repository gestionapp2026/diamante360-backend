package com.eldiamante360.auth.infrastructure.persistence.repository;

import com.eldiamante360.auth.infrastructure.persistence.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioJpaRepository extends JpaRepository<UsuarioEntity, Long> {

    Optional<UsuarioEntity> findByUsername(String username);

    boolean existsByUsername(String username);
}
