package com.eldiamante360.auth.infrastructure.mapper;

import com.eldiamante360.auth.domain.model.Rol;
import com.eldiamante360.auth.infrastructure.persistence.entity.RolEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = PermisoMapper.class)
public interface RolMapper {

    Rol toDomain(RolEntity entity);
}
