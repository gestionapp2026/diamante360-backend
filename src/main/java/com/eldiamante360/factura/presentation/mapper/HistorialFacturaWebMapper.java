package com.eldiamante360.factura.presentation.mapper;

import com.eldiamante360.factura.application.dto.HistorialFacturaResult;
import com.eldiamante360.factura.presentation.dto.response.HistorialFacturaResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface HistorialFacturaWebMapper {

    HistorialFacturaResponse toResponse(HistorialFacturaResult result);
}
