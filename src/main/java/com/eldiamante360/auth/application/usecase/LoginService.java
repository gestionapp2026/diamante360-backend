package com.eldiamante360.auth.application.usecase;

import com.eldiamante360.auth.application.dto.LoginCommand;
import com.eldiamante360.auth.application.dto.SesionResult;
import com.eldiamante360.auth.application.port.PasswordEncoderPort;
import com.eldiamante360.auth.application.port.RefreshTokenRepositoryPort;
import com.eldiamante360.auth.application.port.TokenServicePort;
import com.eldiamante360.auth.application.port.UsuarioRepositoryPort;
import com.eldiamante360.auth.domain.exception.CredencialesInvalidasException;
import com.eldiamante360.auth.domain.model.RefreshToken;
import com.eldiamante360.auth.domain.model.Usuario;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Transactional
public class LoginService implements LoginUseCase {

    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final RefreshTokenRepositoryPort refreshTokenRepositoryPort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final TokenServicePort tokenServicePort;

    public LoginService(UsuarioRepositoryPort usuarioRepositoryPort,
                         RefreshTokenRepositoryPort refreshTokenRepositoryPort,
                         PasswordEncoderPort passwordEncoderPort,
                         TokenServicePort tokenServicePort) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.refreshTokenRepositoryPort = refreshTokenRepositoryPort;
        this.passwordEncoderPort = passwordEncoderPort;
        this.tokenServicePort = tokenServicePort;
    }

    @Override
    public SesionResult ejecutar(LoginCommand command) {
        Usuario usuario = usuarioRepositoryPort.buscarPorUsername(command.username())
                .orElseThrow(CredencialesInvalidasException::new);

        usuario.verificarPuedeAutenticar();

        if (!passwordEncoderPort.matches(command.password(), usuario.getPasswordHash())) {
            throw new CredencialesInvalidasException();
        }

        usuario.registrarLogin(Instant.now());
        usuarioRepositoryPort.guardar(usuario);

        return emitirSesion(usuario, command.ipOrigen(), command.userAgent());
    }

    private SesionResult emitirSesion(Usuario usuario, String ip, String userAgent) {
        String accessToken = tokenServicePort.generarAccessToken(usuario);
        String refreshTokenPlano = tokenServicePort.generarRefreshTokenOpaco();
        String refreshTokenHash = tokenServicePort.hashRefreshToken(refreshTokenPlano);
        Instant expiracion = Instant.now().plusSeconds(tokenServicePort.refreshTokenExpiracionSegundos());

        RefreshToken refreshToken = RefreshToken.nuevo(usuario.getId(), refreshTokenHash, expiracion, ip, userAgent);
        refreshTokenRepositoryPort.guardar(refreshToken);

        return new SesionResult(
                accessToken,
                refreshTokenPlano,
                tokenServicePort.accessTokenExpiracionSegundos(),
                UsuarioAssembler.toResumen(usuario)
        );
    }
}
