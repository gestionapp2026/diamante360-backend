package com.eldiamante360.orden.infrastructure.mapper;

import com.eldiamante360.orden.domain.model.DetalleOrden;
import com.eldiamante360.orden.infrastructure.persistence.entity.DetalleOrdenEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DetalleOrdenMapper {

    DetalleOrden toDomain(DetalleOrdenEntity entity);
}
