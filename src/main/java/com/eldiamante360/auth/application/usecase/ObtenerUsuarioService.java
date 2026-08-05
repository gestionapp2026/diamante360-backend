package com.eldiamante360.auth.application.usecase;

import com.eldiamante360.auth.application.dto.UsuarioResult;
import com.eldiamante360.auth.application.port.UsuarioRepositoryPort;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ObtenerUsuarioService implements ObtenerUsuarioUseCase {

    private final UsuarioRepositoryPort usuarioRepositoryPort;

    public ObtenerUsuarioService(UsuarioRepositoryPort usuarioRepositoryPort) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
    }

    @Override
    public UsuarioResult ejecutar(Long usuarioId) {
        return usuarioRepositoryPort.buscarPorId(usuarioId)
                .map(UsuarioAssembler::toResult)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario", usuarioId));
    }
}
