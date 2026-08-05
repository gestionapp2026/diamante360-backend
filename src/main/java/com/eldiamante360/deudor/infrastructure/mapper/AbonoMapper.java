package com.eldiamante360.deudor.infrastructure.mapper;

import com.eldiamante360.deudor.domain.model.Abono;
import com.eldiamante360.deudor.infrastructure.persistence.entity.AbonoEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AbonoMapper {

    Abono toDomain(AbonoEntity entity);
}
