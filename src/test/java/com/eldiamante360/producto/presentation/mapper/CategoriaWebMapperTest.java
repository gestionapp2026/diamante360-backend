package com.eldiamante360.producto.presentation.mapper;

import com.eldiamante360.producto.application.dto.ActualizarCategoriaCommand;
import com.eldiamante360.producto.application.dto.CategoriaResult;
import com.eldiamante360.producto.application.dto.CrearCategoriaCommand;
import com.eldiamante360.producto.presentation.dto.request.ActualizarCategoriaRequest;
import com.eldiamante360.producto.presentation.dto.request.CrearCategoriaRequest;
import com.eldiamante360.producto.presentation.dto.response.CategoriaResponse;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class CategoriaWebMapperTest {

    private final CategoriaWebMapper mapper = Mappers.getMapper(CategoriaWebMapper.class);

    @Test
    void toCommand_desdeCrearCategoriaRequest_mapeaTodosLosCampos() {
        CrearCategoriaRequest request = new CrearCategoriaRequest("Ahumados", "Productos ahumados");

        CrearCategoriaCommand command = mapper.toCommand(request);

        assertThat(command.nombre()).isEqualTo("Ahumados");
        assertThat(command.descripcion()).isEqualTo("Productos ahumados");
    }

    @Test
    void toCommand_desdeIdYActualizarCategoriaRequest_incluyeElId() {
        ActualizarCategoriaRequest request = new ActualizarCategoriaRequest("Ahumados premium", "Linea premium");

        ActualizarCategoriaCommand command = mapper.toCommand(3L, request);

        assertThat(command.categoriaId()).isEqualTo(3L);
        assertThat(command.nombre()).isEqualTo("Ahumados premium");
    }

    @Test
    void toResponse_desdeCategoriaResult_mapeaTodosLosCampos() {
        CategoriaResult result = new CategoriaResult(1L, "Ahumados", "Productos ahumados", true);

        CategoriaResponse response = mapper.toResponse(result);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.nombre()).isEqualTo("Ahumados");
        assertThat(response.activo()).isTrue();
    }
}
