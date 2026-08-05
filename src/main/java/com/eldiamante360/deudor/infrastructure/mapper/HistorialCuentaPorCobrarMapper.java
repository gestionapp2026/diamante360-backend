package com.eldiamante360.deudor.infrastructure.mapper;

import com.eldiamante360.deudor.domain.model.HistorialCuentaPorCobrar;
import com.eldiamante360.deudor.infrastructure.persistence.entity.HistorialCuentaPorCobrarEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface HistorialCuentaPorCobrarMapper {

    @Mapping(target = "fecha", source = "createdAt")
    HistorialCuentaPorCobrar toDomain(HistorialCuentaPorCobrarEntity entity);
}
