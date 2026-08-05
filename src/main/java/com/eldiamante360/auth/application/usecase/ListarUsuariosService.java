package com.eldiamante360.auth.application.usecase;

import com.eldiamante360.auth.application.dto.UsuarioResult;
import com.eldiamante360.auth.application.port.UsuarioRepositoryPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ListarUsuariosService implements ListarUsuariosUseCase {

    private final UsuarioRepositoryPort usuarioRepositoryPort;

    public ListarUsuariosService(UsuarioRepositoryPort usuarioRepositoryPort) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
    }

    @Override
    public Page<UsuarioResult> ejecutar(Pageable pageable) {
        return usuarioRepositoryPort.listar(pageable).map(UsuarioAssembler::toResult);
    }
}
