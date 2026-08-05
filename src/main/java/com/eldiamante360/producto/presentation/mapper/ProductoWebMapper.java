package com.eldiamante360.producto.presentation.mapper;

import com.eldiamante360.producto.application.dto.ActualizarProductoCommand;
import com.eldiamante360.producto.application.dto.CrearProductoCommand;
import com.eldiamante360.producto.application.dto.ProductoResult;
import com.eldiamante360.producto.presentation.dto.request.ActualizarProductoRequest;
import com.eldiamante360.producto.presentation.dto.request.CrearProductoRequest;
import com.eldiamante360.producto.presentation.dto.response.ProductoResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductoWebMapper {

    CrearProductoCommand toCommand(CrearProductoRequest request);

    ActualizarProductoCommand toCommand(Long productoId, ActualizarProductoRequest request);

    ProductoResponse toResponse(ProductoResult result);
}
