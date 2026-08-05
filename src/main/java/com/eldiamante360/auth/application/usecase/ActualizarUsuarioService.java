package com.eldiamante360.auth.application.usecase;

import com.eldiamante360.auth.application.dto.ActualizarUsuarioCommand;
import com.eldiamante360.auth.application.dto.UsuarioResult;
import com.eldiamante360.auth.application.port.RolRepositoryPort;
import com.eldiamante360.auth.application.port.UsuarioRepositoryPort;
import com.eldiamante360.auth.domain.model.Rol;
import com.eldiamante360.auth.domain.model.Usuario;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ActualizarUsuarioService implements ActualizarUsuarioUseCase {

    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final RolRepositoryPort rolRepositoryPort;

    public ActualizarUsuarioService(UsuarioRepositoryPort usuarioRepositoryPort, RolRepositoryPort rolRepositoryPort) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.rolRepositoryPort = rolRepositoryPort;
    }

    @Override
    public UsuarioResult ejecutar(ActualizarUsuarioCommand command) {
        Usuario usuario = usuarioRepositoryPort.buscarPorId(command.usuarioId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario", command.usuarioId()));

        Rol rol = rolRepositoryPort.buscarPorId(command.rolId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Rol", command.rolId()));

        usuario.actualizarDatos(command.nombreCompleto(), rol);

        return UsuarioAssembler.toResult(usuarioRepositoryPort.guardar(usuario));
    }
}
