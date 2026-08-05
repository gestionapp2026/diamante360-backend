package com.eldiamante360.formula.infrastructure.mapper;

import com.eldiamante360.formula.domain.model.DetalleFormula;
import com.eldiamante360.formula.infrastructure.persistence.entity.DetalleFormulaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DetalleFormulaMapper {

    DetalleFormula toDomain(DetalleFormulaEntity entity);
}
