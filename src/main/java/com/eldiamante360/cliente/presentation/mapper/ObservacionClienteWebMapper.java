package com.eldiamante360.cliente.presentation.mapper;

import com.eldiamante360.cliente.application.dto.ObservacionClienteResult;
import com.eldiamante360.cliente.application.dto.RegistrarObservacionClienteCommand;
import com.eldiamante360.cliente.presentation.dto.request.RegistrarObservacionRequest;
import com.eldiamante360.cliente.presentation.dto.response.ObservacionClienteResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ObservacionClienteWebMapper {

    RegistrarObservacionClienteCommand toCommand(Long clienteId, RegistrarObservacionRequest request, Long usuarioId);

    ObservacionClienteResponse toResponse(ObservacionClienteResult result);
}
