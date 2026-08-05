package com.eldiamante360.auth.application.usecase;

import com.eldiamante360.auth.application.dto.RefreshTokenCommand;
import com.eldiamante360.auth.application.dto.SesionResult;
import com.eldiamante360.auth.application.port.RefreshTokenRepositoryPort;
import com.eldiamante360.auth.application.port.TokenServicePort;
import com.eldiamante360.auth.application.port.UsuarioRepositoryPort;
import com.eldiamante360.auth.domain.exception.RefreshTokenInvalidoException;
import com.eldiamante360.auth.domain.model.RefreshToken;
import com.eldiamante360.auth.domain.model.Usuario;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Implementa rotacion de refresh tokens con deteccion de reuso: si un
 * token ya revocado se vuelve a presentar, se asume robo y se revocan
 * todas las sesiones activas del usuario.
 */
@Service
@Transactional
public class RefreshTokenService implements RefreshTokenUseCase {

    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final RefreshTokenRepositoryPort refreshTokenRepositoryPort;
    private final TokenServicePort tokenServicePort;

    public RefreshTokenService(UsuarioRepositoryPort usuarioRepositoryPort,
                                RefreshTokenRepositoryPort refreshTokenRepositoryPort,
                                TokenServicePort tokenServicePort) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.refreshTokenRepositoryPort = refreshTokenRepositoryPort;
        this.tokenServicePort = tokenServicePort;
    }

    @Override
    public SesionResult ejecutar(RefreshTokenCommand command) {
        String hash = tokenServicePort.hashRefreshToken(command.refreshTokenPlano());
        RefreshToken tokenActual = refreshTokenRepositoryPort.buscarPorHash(hash)
                .orElseThrow(() -> new RefreshTokenInvalidoException("Refresh token invalido"));

        Instant ahora = Instant.now();

        if (tokenActual.isRevocado()) {
            refreshTokenRepositoryPort.revocarTodosPorUsuario(tokenActual.getUsuarioId(), ahora);
            throw new RefreshTokenInvalidoException(
                    "Se detecto reutilizacion de un refresh token revocado; todas las sesiones fueron cerradas");
        }

        if (tokenActual.getFechaExpiracion().isBefore(ahora)) {
            throw new RefreshTokenInvalidoException("El refresh token ha expirado, inicie sesion nuevamente");
        }

        Usuario usuario = usuarioRepositoryPort.buscarPorId(tokenActual.getUsuarioId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario", tokenActual.getUsuarioId()));
        usuario.verificarPuedeAutenticar();

        tokenActual.revocar(ahora);
        refreshTokenRepositoryPort.guardar(tokenActual);

        return emitirSesion(usuario, tokenActual.getIpOrigen(), tokenActual.getUserAgent());
    }

    private SesionResult emitirSesion(Usuario usuario, String ip, String userAgent) {
        String accessToken = tokenServicePort.generarAccessToken(usuario);
        String refreshTokenPlano = tokenServicePort.generarRefreshTokenOpaco();
        String refreshTokenHash = tokenServicePort.hashRefreshToken(refreshTokenPlano);
        Instant expiracion = Instant.now().plusSeconds(tokenServicePort.refreshTokenExpiracionSegundos());

        RefreshToken nuevoToken = RefreshToken.nuevo(usuario.getId(), refreshTokenHash, expiracion, ip, userAgent);
        refreshTokenRepositoryPort.guardar(nuevoToken);

        return new SesionResult(
                accessToken,
                refreshTokenPlano,
                tokenServicePort.accessTokenExpiracionSegundos(),
                UsuarioAssembler.toResumen(usuario)
        );
    }
}
