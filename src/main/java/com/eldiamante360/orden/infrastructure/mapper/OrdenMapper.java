package com.eldiamante360.orden.infrastructure.mapper;

import com.eldiamante360.orden.domain.model.Orden;
import com.eldiamante360.orden.infrastructure.persistence.entity.OrdenEntity;
import org.mapstruct.Mapper;

/**
 * Solo mapea entidad -> dominio. El sentido dominio -> entidad se arma a
 * mano en el adapter, igual que en factura, porque la lista de detalles
 * necesita la referencia bidireccional a OrdenEntity.
 */
@Mapper(componentModel = "spring", uses = DetalleOrdenMapper.class)
public interface OrdenMapper {

    Orden toDomain(OrdenEntity entity);
}
