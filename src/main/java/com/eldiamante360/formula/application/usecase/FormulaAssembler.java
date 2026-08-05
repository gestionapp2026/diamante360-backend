package com.eldiamante360.formula.application.usecase;

import com.eldiamante360.formula.application.dto.DetalleFormulaResult;
import com.eldiamante360.formula.application.dto.FormulaResult;
import com.eldiamante360.formula.domain.model.DetalleFormula;
import com.eldiamante360.formula.domain.model.Formula;
import com.eldiamante360.insumoquimico.application.port.InsumoQuimicoRepositoryPort;
import com.eldiamante360.insumoquimico.domain.model.InsumoQuimico;

/**
 * Ensambla los DTOs de salida de aplicacion a partir del modelo de dominio.
 * No es un mapper de infraestructura: no conoce JPA ni MapStruct. Resuelve
 * el nombre y la unidad de medida del insumo de cada detalle (necesita el
 * puerto de insumos porque {@link DetalleFormula} solo guarda el id); el
 * enmascaramiento del nombre por permisos (rol planta) es responsabilidad de
 * la capa de presentacion, no de este assembler.
 */
final class FormulaAssembler {

    private FormulaAssembler() {
    }

    static FormulaResult toResult(Formula formula, InsumoQuimicoRepositoryPort insumoQuimicoRepositoryPort) {
        return new FormulaResult(
                formula.getId(),
                formula.getProductoId(),
                formula.getProductoNombre(),
                formula.getCantidadBase(),
                formula.getUnidadBase(),
                formula.isActivo(),
                formula.getDetalles().stream()
                        .map(detalle -> toResult(detalle, insumoQuimicoRepositoryPort))
                        .toList()
        );
    }

    private static DetalleFormulaResult toResult(DetalleFormula detalle, InsumoQuimicoRepositoryPort insumoQuimicoRepositoryPort) {
        InsumoQuimico insumo = insumoQuimicoRepositoryPort.buscarPorId(detalle.insumoId()).orElse(null);
        return new DetalleFormulaResult(
                detalle.id(),
                detalle.numero(),
                detalle.insumoId(),
                insumo != null ? insumo.getNombre() : null,
                detalle.cantidad(),
                insumo != null ? insumo.getUnidadMedida() : null
        );
    }
}
