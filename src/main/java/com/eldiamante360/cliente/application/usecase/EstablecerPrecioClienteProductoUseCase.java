package com.eldiamante360.cliente.application.usecase;

import com.eldiamante360.cliente.application.dto.EstablecerPrecioClienteProductoCommand;
import com.eldiamante360.cliente.application.dto.PrecioClienteProductoResult;

public interface EstablecerPrecioClienteProductoUseCase {

    PrecioClienteProductoResult ejecutar(EstablecerPrecioClienteProductoCommand command);
}
