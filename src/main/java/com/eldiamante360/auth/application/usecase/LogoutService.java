package com.eldiamante360.auth.application.usecase;

import com.eldiamante360.auth.application.dto.LogoutCommand;
import com.eldiamante360.auth.application.port.RefreshTokenRepositoryPort;
import com.eldiamante360.auth.application.port.TokenServicePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Transactional
public class LogoutService implements LogoutUseCase {

    private final RefreshTokenRepositoryPort refreshTokenRepositoryPort;
    private final TokenServicePort tokenServicePort;

    public LogoutService(RefreshTokenRepositoryPort refreshTokenRepositoryPort, TokenServicePort tokenServicePort) {
        this.refreshTokenRepositoryPort = refreshTokenRepositoryPort;
        this.tokenServicePort = tokenServicePort;
    }

    @Override
    public void ejecutar(LogoutCommand command) {
        String hash = tokenServicePort.hashRefreshToken(command.refreshTokenPlano());
        refreshTokenRepositoryPort.buscarPorHash(hash).ifPresent(token -> {
            token.revocar(Instant.now());
            refreshTokenRepositoryPort.guardar(token);
        });
    }
}
