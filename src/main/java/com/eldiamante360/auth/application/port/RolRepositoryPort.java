package com.eldiamante360.auth.application.port;

import com.eldiamante360.auth.domain.model.Rol;

import java.util.List;
import java.util.Optional;

public interface RolRepositoryPort {

    Optional<Rol> buscarPorId(Long id);

    Optional<Rol> buscarPorNombre(String nombre);

    List<Rol> listarTodos();
}
