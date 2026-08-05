package com.eldiamante360.auth.presentation.mapper;

import com.eldiamante360.auth.application.dto.ActualizarUsuarioCommand;
import com.eldiamante360.auth.application.dto.CrearUsuarioCommand;
import com.eldiamante360.auth.application.dto.RestablecerPasswordResult;
import com.eldiamante360.auth.application.dto.UsuarioResult;
import com.eldiamante360.auth.presentation.dto.request.ActualizarUsuarioRequest;
import com.eldiamante360.auth.presentation.dto.request.CrearUsuarioRequest;
import com.eldiamante360.auth.presentation.dto.response.RestablecerPasswordResponse;
import com.eldiamante360.auth.presentation.dto.response.UsuarioResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UsuarioWebMapper {

    CrearUsuarioCommand toCommand(CrearUsuarioRequest request);

    ActualizarUsuarioCommand toCommand(Long usuarioId, ActualizarUsuarioRequest request);

    UsuarioResponse toResponse(UsuarioResult result);

    RestablecerPasswordResponse toResponse(RestablecerPasswordResult result);
}
