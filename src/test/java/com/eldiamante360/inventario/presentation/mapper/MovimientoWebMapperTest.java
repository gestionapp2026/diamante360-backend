package com.eldiamante360.inventario.presentation.mapper;

import com.eldiamante360.inventario.application.dto.MovimientoResult;
import com.eldiamante360.inventario.application.dto.RegistrarMovimientoCommand;
import com.eldiamante360.inventario.domain.model.TipoMovimiento;
import com.eldiamante360.inventario.presentation.dto.request.RegistrarMovimientoRequest;
import com.eldiamante360.inventario.presentation.dto.response.MovimientoResponse;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class MovimientoWebMapperTest {

    private final MovimientoWebMapper mapper = Mappers.getMapper(MovimientoWebMapper.class);

    @Test
    void toCommand_desdeProductoIdRequestYUsuarioId_mapeaTodosLosCampos() {
        RegistrarMovimientoRequest request = new RegistrarMovimientoRequest(TipoMovimiento.ENTRADA,
                BigDecimal.valueOf(5), "Compra a proveedor");

        RegistrarMovimientoCommand command = mapper.toCommand(5L, request, 1L);

        assertThat(command.productoId()).isEqualTo(5L);
        assertThat(command.tipoMovimiento()).isEqualTo(TipoMovimiento.ENTRADA);
        assertThat(command.cantidad()).isEqualByComparingTo(BigDecimal.valueOf(5));
        assertThat(command.motivo()).isEqualTo("Compra a proveedor");
        assertThat(command.usuarioId()).isEqualTo(1L);
    }

    @Test
    void toResponse_desdeMovimientoResult_mapeaTodosLosCampos() {
        Instant fecha = Instant.parse("2026-01-01T10:00:00Z");
        MovimientoResult result = new MovimientoResult(1L, 5L, TipoMovimiento.SALIDA, BigDecimal.valueOf(2),
                BigDecimal.valueOf(8), "Venta", 1L, fecha);

        MovimientoResponse response = mapper.toResponse(result);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.productoId()).isEqualTo(5L);
        assertThat(response.tipoMovimiento()).isEqualTo(TipoMovimiento.SALIDA);
        assertThat(response.fecha()).isEqualTo(fecha);
    }
}
