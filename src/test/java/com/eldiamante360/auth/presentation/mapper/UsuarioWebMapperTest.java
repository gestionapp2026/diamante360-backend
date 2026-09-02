package com.eldiamante360.auth.presentation.mapper;

import com.eldiamante360.auth.application.dto.ActualizarUsuarioCommand;
import com.eldiamante360.auth.application.dto.CrearUsuarioCommand;
import com.eldiamante360.auth.application.dto.UsuarioResult;
import com.eldiamante360.auth.presentation.dto.request.ActualizarUsuarioRequest;
import com.eldiamante360.auth.presentation.dto.request.CrearUsuarioRequest;
import com.eldiamante360.auth.presentation.dto.response.UsuarioResponse;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class UsuarioWebMapperTest {

    private final UsuarioWebMapper mapper = Mappers.getMapper(UsuarioWebMapper.class);

    @Test
    void toCommand_desdeCrearUsuarioRequest_mapeaTodosLosCampos() {
        CrearUsuarioRequest request = new CrearUsuarioRequest("jhon", "Secreta123", "Jhon Perez", 1L);

        CrearUsuarioCommand command = mapper.toCommand(request);

        assertThat(command.username()).isEqualTo("jhon");
        assertThat(command.password()).isEqualTo("Secreta123");
        assertThat(command.nombreCompleto()).isEqualTo("Jhon Perez");
        assertThat(command.rolId()).isEqualTo(1L);
    }

    @Test
    void toCommand_desdeIdYActualizarUsuarioRequest_incluyeElId() {
        ActualizarUsuarioRequest request = new ActualizarUsuarioRequest("jhon.perez", "Jhon Alberto Perez", 2L);

        ActualizarUsuarioCommand command = mapper.toCommand(7L, request);

        assertThat(command.usuarioId()).isEqualTo(7L);
        assertThat(command.username()).isEqualTo("jhon.perez");
        assertThat(command.nombreCompleto()).isEqualTo("Jhon Alberto Perez");
        assertThat(command.rolId()).isEqualTo(2L);
    }

    @Test
    void toResponse_desdeUsuarioResult_mapeaTodosLosCampos() {
        Instant ultimoLogin = Instant.parse("2026-01-01T10:00:00Z");
        UsuarioResult result = new UsuarioResult(5L, "angie", "Angie", 1L, "ADMIN", true, false, ultimoLogin);

        UsuarioResponse response = mapper.toResponse(result);

        assertThat(response.id()).isEqualTo(5L);
        assertThat(response.username()).isEqualTo("angie");
        assertThat(response.rolNombre()).isEqualTo("ADMIN");
        assertThat(response.activo()).isTrue();
        assertThat(response.debeCambiarPassword()).isFalse();
        assertThat(response.ultimoLogin()).isEqualTo(ultimoLogin);
    }
}
