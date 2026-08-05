package com.eldiamante360.insumoquimico.presentation.mapper;

import com.eldiamante360.insumoquimico.application.dto.MovimientoInsumoResult;
import com.eldiamante360.insumoquimico.application.dto.RegistrarEntradaInsumoCommand;
import com.eldiamante360.insumoquimico.application.dto.RegistrarSalidaInsumoCommand;
import com.eldiamante360.insumoquimico.domain.model.TipoMovimientoInsumo;
import com.eldiamante360.insumoquimico.presentation.dto.request.RegistrarEntradaInsumoRequest;
import com.eldiamante360.insumoquimico.presentation.dto.request.RegistrarSalidaInsumoRequest;
import com.eldiamante360.insumoquimico.presentation.dto.response.MovimientoInsumoResponse;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class MovimientoInsumoWebMapperTest {

    private final MovimientoInsumoWebMapper mapper = Mappers.getMapper(MovimientoInsumoWebMapper.class);

    @Test
    void toCommand_desdeRegistrarEntradaInsumoRequest_incluyeInsumoIdYUsuarioId() {
        RegistrarEntradaInsumoRequest request = new RegistrarEntradaInsumoRequest("L-001",
                LocalDate.now().plusDays(30), BigDecimal.TEN, "Compra inicial");

        RegistrarEntradaInsumoCommand command = mapper.toCommand(5L, request, 1L);

        assertThat(command.insumoId()).isEqualTo(5L);
        assertThat(command.numeroLote()).isEqualTo("L-001");
        assertThat(command.cantidad()).isEqualByComparingTo(BigDecimal.TEN);
        assertThat(command.motivo()).isEqualTo("Compra inicial");
        assertThat(command.usuarioId()).isEqualTo(1L);
    }

    @Test
    void toCommand_desdeRegistrarSalidaInsumoRequest_incluyeInsumoIdYUsuarioId() {
        RegistrarSalidaInsumoRequest request = new RegistrarSalidaInsumoRequest(100L, BigDecimal.valueOf(4),
                "Uso en produccion");

        RegistrarSalidaInsumoCommand command = mapper.toCommand(5L, request, 1L);

        assertThat(command.insumoId()).isEqualTo(5L);
        assertThat(command.loteId()).isEqualTo(100L);
        assertThat(command.cantidad()).isEqualByComparingTo(BigDecimal.valueOf(4));
        assertThat(command.motivo()).isEqualTo("Uso en produccion");
        assertThat(command.usuarioId()).isEqualTo(1L);
    }

    @Test
    void toResponse_desdeMovimientoInsumoResult_mapeaTodosLosCampos() {
        Instant fecha = Instant.now();
        MovimientoInsumoResult result = new MovimientoInsumoResult(200L, 5L, 100L, TipoMovimientoInsumo.SALIDA,
                BigDecimal.valueOf(4), BigDecimal.valueOf(6), "Uso en produccion", 1L, fecha);

        MovimientoInsumoResponse response = mapper.toResponse(result);

        assertThat(response.id()).isEqualTo(200L);
        assertThat(response.insumoId()).isEqualTo(5L);
        assertThat(response.loteId()).isEqualTo(100L);
        assertThat(response.tipoMovimiento()).isEqualTo(TipoMovimientoInsumo.SALIDA);
        assertThat(response.stockResultante()).isEqualByComparingTo(BigDecimal.valueOf(6));
        assertThat(response.fecha()).isEqualTo(fecha);
    }
}
