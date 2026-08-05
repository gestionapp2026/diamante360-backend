package com.eldiamante360.formula.infrastructure.mapper;

import com.eldiamante360.formula.domain.model.Formula;
import com.eldiamante360.formula.infrastructure.persistence.entity.FormulaEntity;
import org.mapstruct.Mapper;

/**
 * Solo mapea entidad -> dominio. El sentido dominio -> entidad se arma a
 * mano en el adapter (igual que en orden/factura) porque la lista de
 * detalles necesita la referencia bidireccional a FormulaEntity.
 */
@Mapper(componentModel = "spring", uses = DetalleFormulaMapper.class)
public interface FormulaMapper {

    Formula toDomain(FormulaEntity entity);
}
