package com.eldiamante360.producto.infrastructure.mapper;

import com.eldiamante360.producto.domain.model.CategoriaProducto;
import com.eldiamante360.producto.infrastructure.persistence.entity.CategoriaProductoEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoriaProductoMapper {

    CategoriaProducto toDomain(CategoriaProductoEntity entity);
}
