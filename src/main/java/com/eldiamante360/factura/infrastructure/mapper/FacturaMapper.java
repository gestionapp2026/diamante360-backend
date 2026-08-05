package com.eldiamante360.factura.infrastructure.mapper;

import com.eldiamante360.factura.domain.model.Factura;
import com.eldiamante360.factura.infrastructure.persistence.entity.FacturaEntity;
import org.mapstruct.Mapper;

/**
 * Solo mapea entidad -> dominio. El sentido dominio -> entidad se arma a
 * mano en el adapter, igual que en el modulo cliente, porque la lista de
 * detalles necesita la referencia bidireccional a FacturaEntity.
 */
@Mapper(componentModel = "spring", uses = DetalleFacturaMapper.class)
public interface FacturaMapper {

    Factura toDomain(FacturaEntity entity);
}
