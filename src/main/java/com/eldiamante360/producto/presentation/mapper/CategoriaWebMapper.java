package com.eldiamante360.producto.presentation.mapper;

import com.eldiamante360.producto.application.dto.ActualizarCategoriaCommand;
import com.eldiamante360.producto.application.dto.CategoriaResult;
import com.eldiamante360.producto.application.dto.CrearCategoriaCommand;
import com.eldiamante360.producto.presentation.dto.request.ActualizarCategoriaRequest;
import com.eldiamante360.producto.presentation.dto.request.CrearCategoriaRequest;
import com.eldiamante360.producto.presentation.dto.response.CategoriaResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoriaWebMapper {

    CrearCategoriaCommand toCommand(CrearCategoriaRequest request);

    ActualizarCategoriaCommand toCommand(Long categoriaId, ActualizarCategoriaRequest request);

    CategoriaResponse toResponse(CategoriaResult result);
}
