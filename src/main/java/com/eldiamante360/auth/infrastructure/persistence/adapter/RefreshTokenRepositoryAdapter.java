package com.eldiamante360.auth.infrastructure.persistence.adapter;

import com.eldiamante360.auth.application.port.RefreshTokenRepositoryPort;
import com.eldiamante360.auth.domain.model.RefreshToken;
import com.eldiamante360.auth.infrastructure.mapper.RefreshTokenMapper;
import com.eldiamante360.auth.infrastructure.persistence.entity.RefreshTokenEntity;
import com.eldiamante360.auth.infrastructure.persistence.repository.RefreshTokenJpaRepository;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

@Component
public class RefreshTokenRepositoryAdapter implements RefreshTokenRepositoryPort {

    private final RefreshTokenJpaRepository refreshTokenJpaRepository;
    private final RefreshTokenMapper refreshTokenMapper;

    public RefreshTokenRepositoryAdapter(RefreshTokenJpaRepository refreshTokenJpaRepository,
                                          RefreshTokenMapper refreshTokenMapper) {
        this.refreshTokenJpaRepository = refreshTokenJpaRepository;
        this.refreshTokenMapper = refreshTokenMapper;
    }

    @Override
    public RefreshToken guardar(RefreshToken refreshToken) {
        RefreshTokenEntity entity = refreshTokenMapper.toEntity(refreshToken);
        RefreshTokenEntity guardado = refreshTokenJpaRepository.save(entity);
        return refreshTokenMapper.toDomain(guardado);
    }

    @Override
    public Optional<RefreshToken> buscarPorHash(String tokenHash) {
        return refreshTokenJpaRepository.findByTokenHash(tokenHash).map(refreshTokenMapper::toDomain);
    }

    @Override
    public void revocarTodosPorUsuario(Long usuarioId, Instant momento) {
        refreshTokenJpaRepository.revocarTodosPorUsuario(usuarioId, momento);
    }
}
