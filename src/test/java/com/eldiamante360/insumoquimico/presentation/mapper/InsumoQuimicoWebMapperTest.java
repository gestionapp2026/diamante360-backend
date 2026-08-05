package com.eldiamante360.insumoquimico.presentation.mapper;

import com.eldiamante360.insumoquimico.application.dto.CrearInsumoQuimicoCommand;
import com.eldiamante360.insumoquimico.application.dto.InsumoQuimicoResult;
import com.eldiamante360.insumoquimico.application.dto.LoteResult;
import com.eldiamante360.insumoquimico.domain.model.UnidadMedidaInsumo;
import com.eldiamante360.insumoquimico.presentation.dto.request.CrearInsumoQuimicoRequest;
import com.eldiamante360.insumoquimico.presentation.dto.response.InsumoQuimicoResponse;
import com.eldiamante360.insumoquimico.presentation.dto.response.LoteResponse;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class InsumoQuimicoWebMapperTest {

    private final InsumoQuimicoWebMapper mapper = Mappers.getMapper(InsumoQuimicoWebMapper.class);

    @Test
    void toCommand_desdeCrearInsumoQuimicoRequest_mapeaTodosLosCampos() {
        CrearInsumoQuimicoRequest request = new CrearInsumoQuimicoRequest("Sal de cura", UnidadMedidaInsumo.KG);

        CrearInsumoQuimicoCommand command = mapper.toCommand(request);

        assertThat(command.nombre()).isEqualTo("Sal de cura");
        assertThat(command.unidadMedida()).isEqualTo(UnidadMedidaInsumo.KG);
    }

    @Test
    void toResponse_desdeInsumoQuimicoResult_mapeaTodosLosCampos() {
        InsumoQuimicoResult result = new InsumoQuimicoResult(5L, "Sal de cura", UnidadMedidaInsumo.KG,
                BigDecimal.TEN, true, BigDecimal.valueOf(12.5));

        InsumoQuimicoResponse response = mapper.toResponse(result);

        assertThat(response.id()).isEqualTo(5L);
        assertThat(response.nombre()).isEqualTo("Sal de cura");
        assertThat(response.unidadMedida()).isEqualTo(UnidadMedidaInsumo.KG);
        assertThat(response.stockActual()).isEqualByComparingTo(BigDecimal.TEN);
        assertThat(response.activo()).isTrue();
        assertThat(response.precioCompra()).isEqualByComparingTo(BigDecimal.valueOf(12.5));
    }

    @Test
    void toResponse_desdeLoteResult_mapeaTodosLosCampos() {
        Instant fechaIngreso = Instant.now();
        LoteResult result = new LoteResult(100L, 5L, "L-001", LocalDate.now().plusDays(30), BigDecimal.TEN,
                fechaIngreso, false);

        LoteResponse response = mapper.toResponse(result);

        assertThat(response.id()).isEqualTo(100L);
        assertThat(response.insumoId()).isEqualTo(5L);
        assertThat(response.numeroLote()).isEqualTo("L-001");
        assertThat(response.cantidadActual()).isEqualByComparingTo(BigDecimal.TEN);
        assertThat(response.fechaIngreso()).isEqualTo(fechaIngreso);
        assertThat(response.vencido()).isFalse();
    }
}
