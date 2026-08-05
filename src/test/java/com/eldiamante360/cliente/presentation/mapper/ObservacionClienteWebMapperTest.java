package com.eldiamante360.cliente.presentation.mapper;

import com.eldiamante360.cliente.application.dto.ObservacionClienteResult;
import com.eldiamante360.cliente.application.dto.RegistrarObservacionClienteCommand;
import com.eldiamante360.cliente.presentation.dto.request.RegistrarObservacionRequest;
import com.eldiamante360.cliente.presentation.dto.response.ObservacionClienteResponse;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class ObservacionClienteWebMapperTest {

    private final ObservacionClienteWebMapper mapper = Mappers.getMapper(ObservacionClienteWebMapper.class);

    @Test
    void toCommand_desdeIdYRegistrarObservacionRequest_mapeaTodosLosCampos() {
        RegistrarObservacionRequest request = new RegistrarObservacionRequest("Cliente prefiere entregas en la manana");

        RegistrarObservacionClienteCommand command = mapper.toCommand(5L, request, 1L);

        assertThat(command.clienteId()).isEqualTo(5L);
        assertThat(command.texto()).isEqualTo("Cliente prefiere entregas en la manana");
        assertThat(command.usuarioId()).isEqualTo(1L);
    }

    @Test
    void toResponse_desdeObservacionClienteResult_mapeaTodosLosCampos() {
        Instant fecha = Instant.now();
        ObservacionClienteResult result = new ObservacionClienteResult(20L, 5L, "Observacion", 1L, fecha);

        ObservacionClienteResponse response = mapper.toResponse(result);

        assertThat(response.id()).isEqualTo(20L);
        assertThat(response.clienteId()).isEqualTo(5L);
        assertThat(response.texto()).isEqualTo("Observacion");
        assertThat(response.fecha()).isEqualTo(fecha);
    }
}
