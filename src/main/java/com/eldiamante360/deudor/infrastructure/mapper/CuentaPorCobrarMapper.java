package com.eldiamante360.deudor.infrastructure.mapper;

import com.eldiamante360.deudor.domain.model.CuentaPorCobrar;
import com.eldiamante360.deudor.infrastructure.persistence.entity.CuentaPorCobrarEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CuentaPorCobrarMapper {

    CuentaPorCobrar toDomain(CuentaPorCobrarEntity entity);
}
