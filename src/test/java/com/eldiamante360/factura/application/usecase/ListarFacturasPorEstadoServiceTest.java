package com.eldiamante360.factura.application.usecase;

import com.eldiamante360.factura.application.dto.FacturaResult;
import com.eldiamante360.factura.application.port.FacturaRepositoryPort;
import com.eldiamante360.factura.domain.model.DetalleFactura;
import com.eldiamante360.factura.domain.model.EstadoFactura;
import com.eldiamante360.factura.domain.model.Factura;
import com.eldiamante360.factura.domain.model.TipoPago;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListarFacturasPorEstadoServiceTest {

    @Mock
    private FacturaRepositoryPort facturaRepositoryPort;

    private ListarFacturasPorEstadoService service;

    @BeforeEach
    void setUp() {
        service = new ListarFacturasPorEstadoService(facturaRepositoryPort);
    }

    @Test
    void ejecutar_conEstadoAnulada_retornaLaPaginaMapeada() {
        DetalleFactura detalle = DetalleFactura.nuevo(5L, "Chorizo", BigDecimal.valueOf(2), BigDecimal.valueOf(1500), BigDecimal.ZERO);
        Factura factura = new Factura(100L, "FAC-2026-00001", 1L, "Juan Perez", "123456789", TipoPago.CONTADO,
                List.of(detalle), EstadoFactura.ANULADA, BigDecimal.valueOf(3000), BigDecimal.ZERO,
                BigDecimal.valueOf(3000), 9L, "Ana Gomez", Instant.now(), Instant.now(), 0, MedioPago.EFECTIVO);
        Pageable pageable = PageRequest.of(0, 10);
        when(facturaRepositoryPort.listarPorEstado(EstadoFactura.ANULADA, pageable)).thenReturn(new PageImpl<>(List.of(factura)));

        Page<FacturaResult> resultado = service.ejecutar(EstadoFactura.ANULADA, pageable);

        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).estado()).isEqualTo(EstadoFactura.ANULADA);
    }
}
