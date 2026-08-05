package com.eldiamante360.formula.application.dto;

import com.eldiamante360.insumoquimico.domain.model.UnidadMedidaInsumo;

import java.math.BigDecimal;
import java.util.List;

public record CrearFormulaCommand(
        Long productoId,
        BigDecimal cantidadBase,
        UnidadMedidaInsumo unidadBase,
        List<DetalleFormulaCommand> detalles
) {
}
