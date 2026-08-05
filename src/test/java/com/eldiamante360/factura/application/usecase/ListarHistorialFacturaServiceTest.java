package com.eldiamante360.factura.application.usecase;

import com.eldiamante360.factura.application.dto.HistorialFacturaResult;
import com.eldiamante360.factura.application.port.FacturaRepositoryPort;
import com.eldiamante360.factura.application.port.HistorialFacturaRepositoryPort;
import com.eldiamante360.factura.domain.model.DetalleFactura;
import com.eldiamante360.factura.domain.model.EstadoFactura;
import com.eldiamante360.factura.domain.model.Factura;
import com.eldiamante360.factura.domain.model.HistorialFactura;
import com.eldiamante360.factura.domain.model.TipoEventoFactura;
import com.eldiamante360.factura.domain.model.TipoPago;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import com.eldiamante360.shared.domain.model.MedioPago;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListarHistorialFacturaServiceTest {

    @Mock
    private FacturaRepositoryPort facturaRepositoryPort;

    @Mock
    private HistorialFacturaRepositoryPort historialFacturaRepositoryPort;

    private ListarHistorialFacturaService service;

    @BeforeEach
    void setUp() {
        service = new ListarHistorialFacturaService(facturaRepositoryPort, historialFacturaRepositoryPort);
    }

    @Test
    void ejecutar_conFacturaExistente_retornaLaPaginaMapeada() {
        DetalleFactura detalle = DetalleFactura.nuevo(5L, "Chorizo", BigDecimal.valueOf(2), BigDecimal.valueOf(1500), BigDecimal.ZERO);
        Factura factura = new Factura(100L, "FAC-2026-00001", 1L, "Juan Perez", "123456789", TipoPago.CONTADO,
                List.of(detalle), EstadoFactura.EMITIDA, BigDecimal.valueOf(3000), BigDecimal.ZERO,
                BigDecimal.valueOf(3000), 9L, "Ana Gomez", Instant.now(), null, 0, MedioPago.EFECTIVO);
        HistorialFactura historial = HistorialFactura.nuevo(100L, TipoEventoFactura.CREACION,
                "Factura FAC-2026-00001 generada", 9L);
        Pageable pageable = PageRequest.of(0, 10);
        when(facturaRepositoryPort.buscarPorId(100L)).thenReturn(Optional.of(factura));
        when(historialFacturaRepositoryPort.listarPorFactura(100L, pageable)).thenReturn(new PageImpl<>(List.of(historial)));

        Page<HistorialFacturaResult> resultado = service.ejecutar(100L, pageable);

        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).tipoEvento()).isEqualTo(TipoEventoFactura.CREACION);
    }

    @Test
    void ejecutar_conFacturaInexistente_lanzaExcepcion() {
        Pageable pageable = PageRequest.of(0, 10);
        when(facturaRepositoryPort.buscarPorId(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.ejecutar(404L, pageable))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }
}
