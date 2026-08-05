package com.eldiamante360.insumoquimico.infrastructure.mapper;

import com.eldiamante360.insumoquimico.domain.model.MovimientoInsumo;
import com.eldiamante360.insumoquimico.infrastructure.persistence.entity.MovimientoInsumoEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MovimientoInsumoMapper {

    MovimientoInsumo toDomain(MovimientoInsumoEntity entity);
}
