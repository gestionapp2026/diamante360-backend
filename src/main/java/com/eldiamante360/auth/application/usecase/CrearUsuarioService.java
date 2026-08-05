package com.eldiamante360.auth.application.usecase;

import com.eldiamante360.auth.application.dto.CrearUsuarioCommand;
import com.eldiamante360.auth.application.dto.UsuarioResult;
import com.eldiamante360.auth.application.port.PasswordEncoderPort;
import com.eldiamante360.auth.application.port.RolRepositoryPort;
import com.eldiamante360.auth.application.port.UsuarioRepositoryPort;
import com.eldiamante360.auth.domain.exception.NombreUsuarioDuplicadoException;
import com.eldiamante360.auth.domain.model.Rol;
import com.eldiamante360.auth.domain.model.Usuario;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CrearUsuarioService implements CrearUsuarioUseCase {

    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final RolRepositoryPort rolRepositoryPort;
    private final PasswordEncoderPort passwordEncoderPort;

    public CrearUsuarioService(UsuarioRepositoryPort usuarioRepositoryPort,
                                RolRepositoryPort rolRepositoryPort,
                                PasswordEncoderPort passwordEncoderPort) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.rolRepositoryPort = rolRepositoryPort;
        this.passwordEncoderPort = passwordEncoderPort;
    }

    @Override
    public UsuarioResult ejecutar(CrearUsuarioCommand command) {
        if (usuarioRepositoryPort.existePorUsername(command.username())) {
            throw new NombreUsuarioDuplicadoException(command.username());
        }

        Rol rol = rolRepositoryPort.buscarPorId(command.rolId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Rol", command.rolId()));

        String passwordHash = passwordEncoderPort.encode(command.password());
        Usuario usuario = Usuario.nuevo(command.username(), passwordHash, command.nombreCompleto(), rol);

        Usuario guardado = usuarioRepositoryPort.guardar(usuario);
        return UsuarioAssembler.toResult(guardado);
    }
}
