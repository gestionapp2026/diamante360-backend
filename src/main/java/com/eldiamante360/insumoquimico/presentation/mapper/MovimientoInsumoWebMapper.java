package com.eldiamante360.insumoquimico.presentation.mapper;

import com.eldiamante360.insumoquimico.application.dto.MovimientoInsumoResult;
import com.eldiamante360.insumoquimico.application.dto.RegistrarEntradaInsumoCommand;
import com.eldiamante360.insumoquimico.application.dto.RegistrarSalidaInsumoCommand;
import com.eldiamante360.insumoquimico.presentation.dto.request.RegistrarEntradaInsumoRequest;
import com.eldiamante360.insumoquimico.presentation.dto.request.RegistrarSalidaInsumoRequest;
import com.eldiamante360.insumoquimico.presentation.dto.response.MovimientoInsumoResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MovimientoInsumoWebMapper {

    RegistrarEntradaInsumoCommand toCommand(Long insumoId, RegistrarEntradaInsumoRequest request, Long usuarioId);

    RegistrarSalidaInsumoCommand toCommand(Long insumoId, RegistrarSalidaInsumoRequest request, Long usuarioId);

    MovimientoInsumoResponse toResponse(MovimientoInsumoResult result);
}
