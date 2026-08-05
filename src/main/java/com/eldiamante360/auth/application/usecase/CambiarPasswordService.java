package com.eldiamante360.auth.application.usecase;

import com.eldiamante360.auth.application.dto.CambiarPasswordCommand;
import com.eldiamante360.auth.application.port.PasswordEncoderPort;
import com.eldiamante360.auth.application.port.UsuarioRepositoryPort;
import com.eldiamante360.auth.domain.exception.PasswordActualIncorrectaException;
import com.eldiamante360.auth.domain.model.Usuario;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CambiarPasswordService implements CambiarPasswordUseCase {

    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final PasswordEncoderPort passwordEncoderPort;

    public CambiarPasswordService(UsuarioRepositoryPort usuarioRepositoryPort, PasswordEncoderPort passwordEncoderPort) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.passwordEncoderPort = passwordEncoderPort;
    }

    @Override
    public void ejecutar(CambiarPasswordCommand command) {
        Usuario usuario = usuarioRepositoryPort.buscarPorId(command.usuarioId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario", command.usuarioId()));

        if (!passwordEncoderPort.matches(command.passwordActual(), usuario.getPasswordHash())) {
            throw new PasswordActualIncorrectaException();
        }

        usuario.cambiarPassword(passwordEncoderPort.encode(command.passwordNueva()));
        usuarioRepositoryPort.guardar(usuario);
    }
}
