package com.eldiamante360.cliente.presentation.mapper;

import com.eldiamante360.cliente.application.dto.ActualizarClienteCommand;
import com.eldiamante360.cliente.application.dto.AsignarRutaClienteCommand;
import com.eldiamante360.cliente.application.dto.ClienteResult;
import com.eldiamante360.cliente.application.dto.CrearClienteCommand;
import com.eldiamante360.cliente.domain.model.TipoDocumentoCliente;
import com.eldiamante360.cliente.presentation.dto.request.ActualizarClienteRequest;
import com.eldiamante360.cliente.presentation.dto.request.AsignarRutaRequest;
import com.eldiamante360.cliente.presentation.dto.request.CrearClienteRequest;
import com.eldiamante360.cliente.presentation.dto.response.ClienteResponse;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ClienteWebMapperTest {

    private final ClienteWebMapper mapper = Mappers.getMapper(ClienteWebMapper.class);

    @Test
    void toCommand_desdeCrearClienteRequest_mapeaTodosLosCampos() {
        CrearClienteRequest request = new CrearClienteRequest(TipoDocumentoCliente.CC, "123456789", "Juan Perez",
                List.of("3001234567"), "juan@correo.com", "Calle 1 # 2-3", 2L);

        CrearClienteCommand command = mapper.toCommand(request, 1L);

        assertThat(command.tipoDocumento()).isEqualTo(TipoDocumentoCliente.CC);
        assertThat(command.numeroDocumento()).isEqualTo("123456789");
        assertThat(command.nombre()).isEqualTo("Juan Perez");
        assertThat(command.rutaId()).isEqualTo(2L);
        assertThat(command.usuarioId()).isEqualTo(1L);
    }

    @Test
    void toCommand_desdeIdYActualizarClienteRequest_incluyeElId() {
        ActualizarClienteRequest request = new ActualizarClienteRequest("Juan Perez Gomez", List.of("3007654321"),
                "juan.gomez@correo.com", "Calle 5 # 6-7");

        ActualizarClienteCommand command = mapper.toCommand(5L, request, 1L);

        assertThat(command.clienteId()).isEqualTo(5L);
        assertThat(command.nombre()).isEqualTo("Juan Perez Gomez");
        assertThat(command.usuarioId()).isEqualTo(1L);
    }

    @Test
    void toAsignarRutaCommand_desdeIdYAsignarRutaRequest_mapeaTodosLosCampos() {
        AsignarRutaRequest request = new AsignarRutaRequest(2L);

        AsignarRutaClienteCommand command = mapper.toAsignarRutaCommand(5L, request, 1L);

        assertThat(command.clienteId()).isEqualTo(5L);
        assertThat(command.rutaId()).isEqualTo(2L);
        assertThat(command.usuarioId()).isEqualTo(1L);
    }

    @Test
    void toResponse_desdeClienteResult_mapeaTodosLosCampos() {
        ClienteResult result = new ClienteResult(5L, TipoDocumentoCliente.CC, "123456789", "Juan Perez",
                List.of("3001234567"), "juan@correo.com", "Calle 1 # 2-3", 2L, "Ruta Norte", true);

        ClienteResponse response = mapper.toResponse(result);

        assertThat(response.id()).isEqualTo(5L);
        assertThat(response.nombre()).isEqualTo("Juan Perez");
        assertThat(response.rutaId()).isEqualTo(2L);
        assertThat(response.rutaNombre()).isEqualTo("Ruta Norte");
        assertThat(response.activo()).isTrue();
    }
}
