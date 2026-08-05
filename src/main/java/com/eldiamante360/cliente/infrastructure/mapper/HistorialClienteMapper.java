package com.eldiamante360.cliente.infrastructure.mapper;

import com.eldiamante360.cliente.domain.model.HistorialCliente;
import com.eldiamante360.cliente.infrastructure.persistence.entity.HistorialClienteEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface HistorialClienteMapper {

    @Mapping(target = "fecha", source = "createdAt")
    HistorialCliente toDomain(HistorialClienteEntity entity);
}
