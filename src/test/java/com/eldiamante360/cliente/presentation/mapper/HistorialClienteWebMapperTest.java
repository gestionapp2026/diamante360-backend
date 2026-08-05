package com.eldiamante360.cliente.presentation.mapper;

import com.eldiamante360.cliente.application.dto.HistorialClienteResult;
import com.eldiamante360.cliente.domain.model.TipoEventoCliente;
import com.eldiamante360.cliente.presentation.dto.response.HistorialClienteResponse;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class HistorialClienteWebMapperTest {

    private final HistorialClienteWebMapper mapper = Mappers.getMapper(HistorialClienteWebMapper.class);

    @Test
    void toResponse_desdeHistorialClienteResult_mapeaTodosLosCampos() {
        Instant fecha = Instant.now();
        HistorialClienteResult result = new HistorialClienteResult(30L, 5L, TipoEventoCliente.CREACION,
                "Cliente creado", 1L, fecha);

        HistorialClienteResponse response = mapper.toResponse(result);

        assertThat(response.id()).isEqualTo(30L);
        assertThat(response.clienteId()).isEqualTo(5L);
        assertThat(response.tipoEvento()).isEqualTo(TipoEventoCliente.CREACION);
        assertThat(response.descripcion()).isEqualTo("Cliente creado");
        assertThat(response.fecha()).isEqualTo(fecha);
    }
}
