package com.eldiamante360.auth.application.usecase;

import com.eldiamante360.auth.domain.model.Rol;

import java.util.List;

public interface ListarRolesUseCase {

    List<Rol> ejecutar();
}
