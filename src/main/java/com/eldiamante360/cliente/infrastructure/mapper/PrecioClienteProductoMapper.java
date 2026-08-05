package com.eldiamante360.cliente.infrastructure.mapper;

import com.eldiamante360.cliente.domain.model.PrecioClienteProducto;
import com.eldiamante360.cliente.infrastructure.persistence.entity.PrecioClienteProductoEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PrecioClienteProductoMapper {

    PrecioClienteProducto toDomain(PrecioClienteProductoEntity entity);
}
