package com.eldiamante360.factura.infrastructure.mapper;

import com.eldiamante360.factura.domain.model.DetalleFactura;
import com.eldiamante360.factura.infrastructure.persistence.entity.DetalleFacturaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DetalleFacturaMapper {

    DetalleFactura toDomain(DetalleFacturaEntity entity);
}
