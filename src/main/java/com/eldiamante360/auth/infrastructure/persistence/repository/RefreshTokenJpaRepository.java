package com.eldiamante360.auth.infrastructure.persistence.repository;

import com.eldiamante360.auth.infrastructure.persistence.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenEntity, Long> {

    Optional<RefreshTokenEntity> findByTokenHash(String tokenHash);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update RefreshTokenEntity r set r.revocado = true, r.fechaRevocacion = :momento " +
            "where r.usuarioId = :usuarioId and r.revocado = false")
    void revocarTodosPorUsuario(@Param("usuarioId") Long usuarioId, @Param("momento") Instant momento);
}
