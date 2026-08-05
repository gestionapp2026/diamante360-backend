package com.eldiamante360.factura.presentation.mapper;

import com.eldiamante360.factura.application.dto.HistorialFacturaResult;
import com.eldiamante360.factura.domain.model.TipoEventoFactura;
import com.eldiamante360.factura.presentation.dto.response.HistorialFacturaResponse;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class HistorialFacturaWebMapperTest {

    private final HistorialFacturaWebMapper mapper = Mappers.getMapper(HistorialFacturaWebMapper.class);

    @Test
    void toResponse_mapeaTodosLosCampos() {
        Instant fecha = Instant.now();
        HistorialFacturaResult result = new HistorialFacturaResult(1L, 100L, TipoEventoFactura.ANULACION,
                "Factura FAC-2026-00001 anulada", 9L, fecha);

        HistorialFacturaResponse response = mapper.toResponse(result);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.facturaId()).isEqualTo(100L);
        assertThat(response.tipoEvento()).isEqualTo(TipoEventoFactura.ANULACION);
        assertThat(response.descripcion()).isEqualTo("Factura FAC-2026-00001 anulada");
        assertThat(response.usuarioId()).isEqualTo(9L);
        assertThat(response.fecha()).isEqualTo(fecha);
    }
}
