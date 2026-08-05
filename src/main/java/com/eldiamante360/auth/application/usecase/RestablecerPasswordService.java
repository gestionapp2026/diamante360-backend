package com.eldiamante360.auth.application.usecase;

import com.eldiamante360.auth.application.dto.RestablecerPasswordResult;
import com.eldiamante360.auth.application.port.PasswordEncoderPort;
import com.eldiamante360.auth.application.port.UsuarioRepositoryPort;
import com.eldiamante360.auth.domain.model.Usuario;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import com.eldiamante360.shared.domain.exception.ReglaNegocioException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RestablecerPasswordService implements RestablecerPasswordUseCase {

    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final PasswordEncoderPort passwordEncoderPort;

    public RestablecerPasswordService(UsuarioRepositoryPort usuarioRepositoryPort, PasswordEncoderPort passwordEncoderPort) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.passwordEncoderPort = passwordEncoderPort;
    }

    @Override
    public RestablecerPasswordResult ejecutar(Long usuarioId, Long solicitanteId) {
        if (usuarioId.equals(solicitanteId)) {
            throw new ReglaNegocioException(
                    "No puede restablecer su propia contrasena por esta via; use la opcion de cambiar su contrasena");
        }

        Usuario usuario = usuarioRepositoryPort.buscarPorId(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario", usuarioId));

        String passwordTemporal = GeneradorPasswordTemporal.generar();
        usuario.restablecerPassword(passwordEncoderPort.encode(passwordTemporal));
        Usuario guardado = usuarioRepositoryPort.guardar(usuario);

        return new RestablecerPasswordResult(guardado.getId(), guardado.getUsername(), passwordTemporal);
    }
}
