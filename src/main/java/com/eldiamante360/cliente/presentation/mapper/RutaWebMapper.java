package com.eldiamante360.cliente.presentation.mapper;

import com.eldiamante360.cliente.application.dto.ActualizarRutaCommand;
import com.eldiamante360.cliente.application.dto.CrearRutaCommand;
import com.eldiamante360.cliente.application.dto.RutaResult;
import com.eldiamante360.cliente.presentation.dto.request.ActualizarRutaRequest;
import com.eldiamante360.cliente.presentation.dto.request.CrearRutaRequest;
import com.eldiamante360.cliente.presentation.dto.response.RutaResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RutaWebMapper {

    CrearRutaCommand toCommand(CrearRutaRequest request);

    ActualizarRutaCommand toCommand(Long rutaId, ActualizarRutaRequest request);

    RutaResponse toResponse(RutaResult result);
}
