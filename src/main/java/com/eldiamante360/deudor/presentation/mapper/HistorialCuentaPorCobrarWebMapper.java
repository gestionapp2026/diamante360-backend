package com.eldiamante360.deudor.presentation.mapper;

import com.eldiamante360.deudor.application.dto.HistorialCuentaPorCobrarResult;
import com.eldiamante360.deudor.presentation.dto.response.HistorialCuentaPorCobrarResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface HistorialCuentaPorCobrarWebMapper {

    HistorialCuentaPorCobrarResponse toResponse(HistorialCuentaPorCobrarResult result);
}
