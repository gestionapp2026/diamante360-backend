package com.eldiamante360.inventario.presentation.mapper;

import com.eldiamante360.inventario.application.dto.MovimientoResult;
import com.eldiamante360.inventario.application.dto.RegistrarMovimientoCommand;
import com.eldiamante360.inventario.presentation.dto.request.RegistrarMovimientoRequest;
import com.eldiamante360.inventario.presentation.dto.response.MovimientoResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MovimientoWebMapper {

    RegistrarMovimientoCommand toCommand(Long productoId, RegistrarMovimientoRequest request, Long usuarioId);

    MovimientoResponse toResponse(MovimientoResult result);
}
