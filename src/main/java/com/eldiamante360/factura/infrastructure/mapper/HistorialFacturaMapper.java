package com.eldiamante360.factura.infrastructure.mapper;

import com.eldiamante360.factura.domain.model.HistorialFactura;
import com.eldiamante360.factura.infrastructure.persistence.entity.HistorialFacturaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface HistorialFacturaMapper {

    @Mapping(target = "fecha", source = "createdAt")
    HistorialFactura toDomain(HistorialFacturaEntity entity);
}
