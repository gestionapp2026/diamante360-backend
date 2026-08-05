package com.eldiamante360.inventario.infrastructure.mapper;

import com.eldiamante360.inventario.domain.model.MovimientoInventario;
import com.eldiamante360.inventario.infrastructure.persistence.entity.MovimientoInventarioEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MovimientoInventarioMapper {

    MovimientoInventario toDomain(MovimientoInventarioEntity entity);
}
