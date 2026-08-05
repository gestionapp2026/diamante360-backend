package com.eldiamante360.cliente.presentation.mapper;

import com.eldiamante360.cliente.application.dto.EstablecerPrecioClienteProductoCommand;
import com.eldiamante360.cliente.application.dto.PrecioClienteProductoResult;
import com.eldiamante360.cliente.presentation.dto.request.EstablecerPrecioClienteProductoRequest;
import com.eldiamante360.cliente.presentation.dto.response.PrecioClienteProductoResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PrecioClienteProductoWebMapper {

    EstablecerPrecioClienteProductoCommand toCommand(Long clienteId, EstablecerPrecioClienteProductoRequest request);

    PrecioClienteProductoResponse toResponse(PrecioClienteProductoResult result);
}
