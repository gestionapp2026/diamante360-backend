package com.eldiamante360.formula.presentation.mapper;

import com.eldiamante360.formula.application.dto.ActualizarFormulaCommand;
import com.eldiamante360.formula.application.dto.CrearFormulaCommand;
import com.eldiamante360.formula.application.dto.FormulaResult;
import com.eldiamante360.formula.application.dto.ProduccionFormulaResult;
import com.eldiamante360.formula.application.dto.ProducirFormulaCommand;
import com.eldiamante360.formula.presentation.dto.request.ActualizarFormulaRequest;
import com.eldiamante360.formula.presentation.dto.request.CrearFormulaRequest;
import com.eldiamante360.formula.presentation.dto.request.ProducirFormulaRequest;
import com.eldiamante360.formula.presentation.dto.response.DetalleFormulaResponse;
import com.eldiamante360.formula.presentation.dto.response.FormulaResponse;
import com.eldiamante360.formula.presentation.dto.response.ProduccionFormulaResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FormulaWebMapper {

    CrearFormulaCommand toCommand(CrearFormulaRequest request);

    ActualizarFormulaCommand toCommand(Long formulaId, ActualizarFormulaRequest request);

    ProducirFormulaCommand toCommand(Long formulaId, ProducirFormulaRequest request, Long usuarioId);

    FormulaResponse toResponse(FormulaResult result);

    ProduccionFormulaResponse toResponse(ProduccionFormulaResult result);

    /**
     * Devuelve una copia de {@code original} con el nombre de cada insumo
     * quimico (en los detalles) enmascarado a {@code null}. Usado cuando el
     * usuario autenticado no tiene el permiso {@code INSUMO_VER_NOMBRE} (por
     * ejemplo, el rol PLANTA): solo debe ver el numero de frasco, nunca el
     * nombre del quimico.
     */
    default FormulaResponse enmascararNombreInsumo(FormulaResponse original) {
        return new FormulaResponse(
                original.id(),
                original.productoId(),
                original.productoNombre(),
                original.cantidadBase(),
                original.unidadBase(),
                original.activo(),
                original.detalles().stream()
                        .map(detalle -> new DetalleFormulaResponse(
                                detalle.id(),
                                detalle.numero(),
                                detalle.insumoId(),
                                null,
                                detalle.cantidad(),
                                detalle.unidadMedidaInsumo()))
                        .toList());
    }
}
