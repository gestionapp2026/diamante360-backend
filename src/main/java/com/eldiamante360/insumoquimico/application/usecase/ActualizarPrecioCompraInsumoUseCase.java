package com.eldiamante360.insumoquimico.application.usecase;

import com.eldiamante360.insumoquimico.application.dto.InsumoQuimicoResult;

import java.math.BigDecimal;

public interface ActualizarPrecioCompraInsumoUseCase {

    InsumoQuimicoResult ejecutar(Long insumoId, BigDecimal precioCompra);
}
