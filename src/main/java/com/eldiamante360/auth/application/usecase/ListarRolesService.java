package com.eldiamante360.auth.application.usecase;

import com.eldiamante360.auth.application.port.RolRepositoryPort;
import com.eldiamante360.auth.domain.model.Rol;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ListarRolesService implements ListarRolesUseCase {

    private final RolRepositoryPort rolRepositoryPort;

    public ListarRolesService(RolRepositoryPort rolRepositoryPort) {
        this.rolRepositoryPort = rolRepositoryPort;
    }

    @Override
    public List<Rol> ejecutar() {
        return rolRepositoryPort.listarTodos();
    }
}
