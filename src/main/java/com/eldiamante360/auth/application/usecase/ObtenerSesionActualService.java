package com.eldiamante360.auth.application.usecase;

import com.eldiamante360.auth.application.dto.UsuarioResumen;
import com.eldiamante360.auth.application.port.UsuarioRepositoryPort;
import com.eldiamante360.auth.domain.model.Usuario;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Permite al frontend rehidratar la sesion (rol, permisos) tras recargar
 * la pagina, a partir del username embebido en el access token vigente.
 */
@Service
@Transactional(readOnly = true)
public class ObtenerSesionActualService implements ObtenerSesionActualUseCase {

    private final UsuarioRepositoryPort usuarioRepositoryPort;

    public ObtenerSesionActualService(UsuarioRepositoryPort usuarioRepositoryPort) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
    }

    @Override
    public UsuarioResumen ejecutar(String username) {
        Usuario usuario = usuarioRepositoryPort.buscarPorUsername(username)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario", username));
        return UsuarioAssembler.toResumen(usuario);
    }
}
