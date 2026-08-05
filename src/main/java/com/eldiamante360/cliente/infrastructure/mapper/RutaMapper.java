package com.eldiamante360.cliente.infrastructure.mapper;

import com.eldiamante360.cliente.domain.model.Ruta;
import com.eldiamante360.cliente.infrastructure.persistence.entity.RutaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RutaMapper {

    Ruta toDomain(RutaEntity entity);
}
