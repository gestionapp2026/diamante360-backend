package com.eldiamante360.cliente.presentation.mapper;

import com.eldiamante360.cliente.application.dto.HistorialClienteResult;
import com.eldiamante360.cliente.presentation.dto.response.HistorialClienteResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface HistorialClienteWebMapper {

    HistorialClienteResponse toResponse(HistorialClienteResult result);
}
