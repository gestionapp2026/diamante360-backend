package com.eldiamante360.cliente.infrastructure.mapper;

import com.eldiamante360.cliente.domain.model.ObservacionCliente;
import com.eldiamante360.cliente.infrastructure.persistence.entity.ObservacionClienteEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ObservacionClienteMapper {

    @Mapping(target = "fecha", source = "createdAt")
    ObservacionCliente toDomain(ObservacionClienteEntity entity);
}
