package com.eldiamante360.auth.application.port;

import com.eldiamante360.auth.domain.model.RefreshToken;

import java.time.Instant;
import java.util.Optional;

public interface RefreshTokenRepositoryPort {

    RefreshToken guardar(RefreshToken refreshToken);

    Optional<RefreshToken> buscarPorHash(String tokenHash);

    void revocarTodosPorUsuario(Long usuarioId, Instant momento);
}
