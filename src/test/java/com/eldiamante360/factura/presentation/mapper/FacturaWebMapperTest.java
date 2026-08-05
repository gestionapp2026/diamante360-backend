package com.eldiamante360.factura.presentation.mapper;

import com.eldiamante360.factura.application.dto.CrearFacturaCommand;
import com.eldiamante360.factura.application.dto.DetalleFacturaResult;
import com.eldiamante360.factura.application.dto.FacturaResult;
import com.eldiamante360.factura.domain.model.EstadoFactura;
import com.eldiamante360.factura.domain.model.TipoPago;
import com.eldiamante360.factura.presentation.dto.request.CrearFacturaRequest;
import com.eldiamante360.factura.presentation.dto.request.DetalleFacturaRequest;
import com.eldiamante360.factura.presentation.dto.response.FacturaResponse;
import com.eldiamante360.shared.domain.model.MedioPago;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FacturaWebMapperTest {

    private final FacturaWebMapper mapper = Mappers.getMapper(FacturaWebMapper.class);

    @Test
    void toCommand_mapeaTodosLosCamposIncluyendoElUsuarioId() {
        DetalleFacturaRequest detalleRequest = new DetalleFacturaRequest(5L, BigDecimal.valueOf(2), BigDecimal.TEN);
        CrearFacturaRequest request = new CrearFacturaRequest(1L, TipoPago.CREDITO, List.of(detalleRequest), null);

        CrearFacturaCommand command = mapper.toCommand(request, 9L);

        assertThat(command.clienteId()).isEqualTo(1L);
        assertThat(command.tipoPago()).isEqualTo(TipoPago.CREDITO);
        assertThat(command.usuarioId()).isEqualTo(9L);
    }

    @Test
    void toCommand_mapeaLaListaDeDetallesConSusValores() {
        DetalleFacturaRequest detalle1 = new DetalleFacturaRequest(5L, BigDecimal.valueOf(2), BigDecimal.TEN);
        DetalleFacturaRequest detalle2 = new DetalleFacturaRequest(6L, BigDecimal.valueOf(3), BigDecimal.ZERO);
        CrearFacturaRequest request = new CrearFacturaRequest(1L, TipoPago.CONTADO, List.of(detalle1, detalle2),
                MedioPago.EFECTIVO);

        CrearFacturaCommand command = mapper.toCommand(request, 9L);

        assertThat(command.detalles()).hasSize(2);
        assertThat(command.detalles().get(0).productoId()).isEqualTo(5L);
        assertThat(command.detalles().get(0).cantidad()).isEqualByComparingTo(BigDecimal.valueOf(2));
        assertThat(command.detalles().get(0).porcentajeDescuento()).isEqualByComparingTo(BigDecimal.TEN);
        assertThat(command.detalles().get(1).productoId()).isEqualTo(6L);
        assertThat(command.detalles().get(1).cantidad()).isEqualByComparingTo(BigDecimal.valueOf(3));
        assertThat(command.detalles().get(1).porcentajeDescuento()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void toResponse_mapeaTodosLosCamposDeCabecera() {
        Instant fecha = Instant.now();
        DetalleFacturaResult detalleResult = new DetalleFacturaResult(1L, 5L, "Chorizo", BigDecimal.valueOf(2),
                BigDecimal.valueOf(1500), BigDecimal.ZERO, BigDecimal.valueOf(3000), BigDecimal.ZERO, BigDecimal.valueOf(3000));
        FacturaResult result = new FacturaResult(100L, "FAC-2026-00001", 1L, "Juan Perez", "123456789",
                TipoPago.CONTADO, List.of(detalleResult), EstadoFactura.EMITIDA, BigDecimal.valueOf(3000),
                BigDecimal.ZERO, BigDecimal.valueOf(3000), 9L, "Ana Gomez", fecha, null, MedioPago.EFECTIVO);

        FacturaResponse response = mapper.toResponse(result);

        assertThat(response.id()).isEqualTo(100L);
        assertThat(response.numero()).isEqualTo("FAC-2026-00001");
        assertThat(response.clienteId()).isEqualTo(1L);
        assertThat(response.clienteNombre()).isEqualTo("Juan Perez");
        assertThat(response.clienteNumeroDocumento()).isEqualTo("123456789");
        assertThat(response.tipoPago()).isEqualTo(TipoPago.CONTADO);
        assertThat(response.estado()).isEqualTo(EstadoFactura.EMITIDA);
        assertThat(response.total()).isEqualByComparingTo(BigDecimal.valueOf(3000));
        assertThat(response.usuarioId()).isEqualTo(9L);
        assertThat(response.usuarioNombre()).isEqualTo("Ana Gomez");
        assertThat(response.fecha()).isEqualTo(fecha);
        assertThat(response.fechaAnulacion()).isNull();
    }

    @Test
    void toResponse_mapeaLaListaDeDetallesConSusValores() {
        DetalleFacturaResult detalle1 = new DetalleFacturaResult(1L, 5L, "Chorizo", BigDecimal.valueOf(2),
                BigDecimal.valueOf(1500), BigDecimal.ZERO, BigDecimal.valueOf(3000), BigDecimal.ZERO, BigDecimal.valueOf(3000));
        DetalleFacturaResult detalle2 = new DetalleFacturaResult(2L, 6L, "Costilla", BigDecimal.valueOf(3),
                BigDecimal.valueOf(2000), BigDecimal.TEN, BigDecimal.valueOf(6000), BigDecimal.valueOf(600), BigDecimal.valueOf(5400));
        FacturaResult result = new FacturaResult(100L, "FAC-2026-00001", 1L, "Juan Perez", "123456789",
                TipoPago.CONTADO, List.of(detalle1, detalle2), EstadoFactura.EMITIDA, BigDecimal.valueOf(9000),
                BigDecimal.valueOf(600), BigDecimal.valueOf(8400), 9L, "Ana Gomez", Instant.now(), null,
                MedioPago.EFECTIVO);

        FacturaResponse response = mapper.toResponse(result);

        assertThat(response.detalles()).hasSize(2);
        assertThat(response.detalles().get(0).productoId()).isEqualTo(5L);
        assertThat(response.detalles().get(0).productoNombre()).isEqualTo("Chorizo");
        assertThat(response.detalles().get(0).total()).isEqualByComparingTo(BigDecimal.valueOf(3000));
        assertThat(response.detalles().get(1).productoId()).isEqualTo(6L);
        assertThat(response.detalles().get(1).productoNombre()).isEqualTo("Costilla");
        assertThat(response.detalles().get(1).descuento()).isEqualByComparingTo(BigDecimal.valueOf(600));
        assertThat(response.detalles().get(1).total()).isEqualByComparingTo(BigDecimal.valueOf(5400));
    }
}
