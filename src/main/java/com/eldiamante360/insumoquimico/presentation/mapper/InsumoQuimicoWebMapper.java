package com.eldiamante360.insumoquimico.presentation.mapper;

import com.eldiamante360.insumoquimico.application.dto.CrearInsumoQuimicoCommand;
import com.eldiamante360.insumoquimico.application.dto.InsumoQuimicoResult;
import com.eldiamante360.insumoquimico.application.dto.LoteResult;
import com.eldiamante360.insumoquimico.presentation.dto.request.CrearInsumoQuimicoRequest;
import com.eldiamante360.insumoquimico.presentation.dto.response.InsumoQuimicoResponse;
import com.eldiamante360.insumoquimico.presentation.dto.response.LoteResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InsumoQuimicoWebMapper {

    CrearInsumoQuimicoCommand toCommand(CrearInsumoQuimicoRequest request);

    InsumoQuimicoResponse toResponse(InsumoQuimicoResult result);

    LoteResponse toResponse(LoteResult result);
}
