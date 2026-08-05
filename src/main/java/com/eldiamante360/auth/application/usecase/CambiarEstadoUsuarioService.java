package com.eldiamante360.auth.application.usecase;

import com.eldiamante360.auth.application.dto.UsuarioResult;
import com.eldiamante360.auth.application.port.UsuarioRepositoryPort;
import com.eldiamante360.auth.domain.model.Usuario;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import com.eldiamante360.shared.domain.exception.ReglaNegocioException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CambiarEstadoUsuarioService implements CambiarEstadoUsuarioUseCase {

    private final UsuarioRepositoryPort usuarioRepositoryPort;

    public CambiarEstadoUsuarioService(UsuarioRepositoryPort usuarioRepositoryPort) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
    }

    @Override
    public UsuarioResult ejecutar(Long usuarioId, boolean activo, Long solicitanteId) {
        if (!activo && usuarioId.equals(solicitanteId)) {
            throw new ReglaNegocioException("No puede desactivar su propio usuario");
        }

        Usuario usuario = usuarioRepositoryPort.buscarPorId(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario", usuarioId));

        if (activo) {
            usuario.activar();
        } else {
            usuario.desactivar();
        }

        return UsuarioAssembler.toResult(usuarioRepositoryPort.guardar(usuario));
    }
}
