package com.eldiamante360.auth.infrastructure.mapper;

import com.eldiamante360.auth.domain.model.Permiso;
import com.eldiamante360.auth.infrastructure.persistence.entity.PermisoEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermisoMapper {

    Permiso toDomain(PermisoEntity entity);
}
