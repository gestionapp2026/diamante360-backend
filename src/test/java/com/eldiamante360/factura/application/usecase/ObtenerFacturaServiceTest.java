package com.eldiamante360.factura.application.usecase;

import com.eldiamante360.factura.application.dto.FacturaResult;
import com.eldiamante360.factura.application.port.FacturaRepositoryPort;
import com.eldiamante360.factura.domain.model.DetalleFactura;
import com.eldiamante360.factura.domain.model.EstadoFactura;
import com.eldiamante360.factura.domain.model.Factura;
import com.eldiamante360.factura.domain.model.TipoPago;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import com.eldiamante360.shared.domain.model.MedioPago;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ObtenerFacturaServiceTest {

    @Mock
    private FacturaRepositoryPort facturaRepositoryPort;

    private ObtenerFacturaService service;

    @BeforeEach
    void setUp() {
        service = new ObtenerFacturaService(facturaRepositoryPort);
    }

    @Test
    void ejecutar_conFacturaExistente_retornaLaFactura() {
        DetalleFactura detalle = DetalleFactura.nuevo(5L, "Chorizo", BigDecimal.valueOf(2), BigDecimal.valueOf(1500), BigDecimal.ZERO);
        Factura factura = new Factura(100L, "FAC-2026-00001", 1L, "Juan Perez", "123456789", TipoPago.CONTADO,
                List.of(detalle), EstadoFactura.EMITIDA, BigDecimal.valueOf(3000), BigDecimal.ZERO,
                BigDecimal.valueOf(3000), 9L, "Ana Gomez", Instant.now(), null, 0, MedioPago.EFECTIVO);
        when(facturaRepositoryPort.buscarPorId(100L)).thenReturn(Optional.of(factura));

        FacturaResult resultado = service.ejecutar(100L);

        assertThat(resultado.id()).isEqualTo(100L);
        assertThat(resultado.numero()).isEqualTo("FAC-2026-00001");
    }

    @Test
    void ejecutar_conFacturaInexistente_lanzaExcepcion() {
        when(facturaRepositoryPort.buscarPorId(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.ejecutar(404L))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }
}
