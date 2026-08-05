package com.eldiamante360.auth.presentation.mapper;

import com.eldiamante360.auth.domain.model.Rol;
import com.eldiamante360.auth.presentation.dto.response.RolResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RolWebMapper {

    @Mapping(target = "permisos", expression = "java(rol.codigosPermisos())")
    RolResponse toResponse(Rol rol);
}
