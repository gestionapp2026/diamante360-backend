package com.eldiamante360.cliente.presentation.mapper;

import com.eldiamante360.cliente.application.dto.ActualizarRutaCommand;
import com.eldiamante360.cliente.application.dto.CrearRutaCommand;
import com.eldiamante360.cliente.application.dto.RutaResult;
import com.eldiamante360.cliente.presentation.dto.request.ActualizarRutaRequest;
import com.eldiamante360.cliente.presentation.dto.request.CrearRutaRequest;
import com.eldiamante360.cliente.presentation.dto.response.RutaResponse;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class RutaWebMapperTest {

    private final RutaWebMapper mapper = Mappers.getMapper(RutaWebMapper.class);

    @Test
    void toCommand_desdeCrearRutaRequest_mapeaTodosLosCampos() {
        CrearRutaRequest request = new CrearRutaRequest("Ruta Norte", "Zona norte");

        CrearRutaCommand command = mapper.toCommand(request);

        assertThat(command.nombre()).isEqualTo("Ruta Norte");
        assertThat(command.descripcion()).isEqualTo("Zona norte");
    }

    @Test
    void toCommand_desdeIdYActualizarRutaRequest_incluyeElId() {
        ActualizarRutaRequest request = new ActualizarRutaRequest("Ruta Norte Extendida", "Zona norte y noroccidente");

        ActualizarRutaCommand command = mapper.toCommand(3L, request);

        assertThat(command.rutaId()).isEqualTo(3L);
        assertThat(command.nombre()).isEqualTo("Ruta Norte Extendida");
    }

    @Test
    void toResponse_desdeRutaResult_mapeaTodosLosCampos() {
        RutaResult result = new RutaResult(2L, "Ruta Norte", "Zona norte", true);

        RutaResponse response = mapper.toResponse(result);

        assertThat(response.id()).isEqualTo(2L);
        assertThat(response.nombre()).isEqualTo("Ruta Norte");
        assertThat(response.activo()).isTrue();
    }
}
