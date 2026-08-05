package com.eldiamante360.insumoquimico.application.usecase;

import com.eldiamante360.insumoquimico.application.dto.InsumoQuimicoResult;
import com.eldiamante360.insumoquimico.application.dto.LoteResult;
import com.eldiamante360.insumoquimico.domain.model.InsumoQuimico;
import com.eldiamante360.insumoquimico.domain.model.LoteInsumo;

import java.time.LocalDate;

/**
 * Ensambla los DTOs de salida de aplicacion a partir del modelo de dominio.
 * No es un mapper de infraestructura: no conoce JPA ni MapStruct.
 */
final class InsumoQuimicoAssembler {

    private InsumoQuimicoAssembler() {
    }

    static InsumoQuimicoResult toResult(InsumoQuimico insumo) {
        return new InsumoQuimicoResult(
                insumo.getId(),
                insumo.getNombre(),
                insumo.getUnidadMedida(),
                insumo.getStockActual(),
                insumo.isActivo(),
                insumo.getPrecioCompra()
        );
    }

    static LoteResult toResult(LoteInsumo lote) {
        return new LoteResult(
                lote.getId(),
                lote.getInsumoId(),
                lote.getNumeroLote(),
                lote.getFechaVencimiento(),
                lote.getCantidadActual(),
                lote.getFechaIngreso(),
                lote.estaVencido(LocalDate.now())
        );
    }
}
