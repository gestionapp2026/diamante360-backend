package com.eldiamante360.insumoquimico.infrastructure.mapper;

import com.eldiamante360.insumoquimico.domain.model.LoteInsumo;
import com.eldiamante360.insumoquimico.infrastructure.persistence.entity.LoteInsumoEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LoteInsumoMapper {

    LoteInsumo toDomain(LoteInsumoEntity entity);
}
