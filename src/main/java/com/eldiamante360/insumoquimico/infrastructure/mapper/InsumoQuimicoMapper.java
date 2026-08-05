package com.eldiamante360.insumoquimico.infrastructure.mapper;

import com.eldiamante360.insumoquimico.domain.model.InsumoQuimico;
import com.eldiamante360.insumoquimico.infrastructure.persistence.entity.InsumoQuimicoEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InsumoQuimicoMapper {

    InsumoQuimico toDomain(InsumoQuimicoEntity entity);
}
