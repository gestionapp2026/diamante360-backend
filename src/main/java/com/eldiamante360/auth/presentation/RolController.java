package com.eldiamante360.auth.presentation;

import com.eldiamante360.auth.application.usecase.ListarRolesUseCase;
import com.eldiamante360.auth.presentation.dto.response.RolResponse;
import com.eldiamante360.auth.presentation.mapper.RolWebMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/roles")
public class RolController {

    private final ListarRolesUseCase listarRolesUseCase;
    private final RolWebMapper rolWebMapper;

    public RolController(ListarRolesUseCase listarRolesUseCase, RolWebMapper rolWebMapper) {
        this.listarRolesUseCase = listarRolesUseCase;
        this.rolWebMapper = rolWebMapper;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROL_GESTIONAR', 'USUARIO_CREAR', 'USUARIO_EDITAR')")
    public List<RolResponse> listar() {
        return listarRolesUseCase.ejecutar().stream().map(rolWebMapper::toResponse).toList();
    }
}
