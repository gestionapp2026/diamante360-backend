package com.eldiamante360.factura.application.usecase;

import com.eldiamante360.cliente.application.port.ClienteRepositoryPort;
import com.eldiamante360.cliente.domain.model.Cliente;
import com.eldiamante360.cliente.domain.model.TipoDocumentoCliente;
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
class ListarFacturasPorClienteServiceTest {

    @Mock
    private ClienteRepositoryPort clienteRepositoryPort;

    @Mock
    private FacturaRepositoryPort facturaRepositoryPort;

    private ListarFacturasPorClienteService service;

    @BeforeEach
    void setUp() {
        service = new ListarFacturasPorClienteService(clienteRepositoryPort, facturaRepositoryPort);
    }

    @Test
    void ejecutar_conClienteExistente_retornaLaPaginaMapeada() {
        Cliente cliente = new Cliente(1L, TipoDocumentoCliente.CC, "123456789", "Juan Perez", "3001234567",
                "juan@correo.com", "Calle 1 # 2-3", null, true, 0);
        DetalleFactura detalle = DetalleFactura.nuevo(5L, "Chorizo", BigDecimal.valueOf(2), BigDecimal.valueOf(1500), BigDecimal.ZERO);
        Factura factura = new Factura(100L, "FAC-2026-00001", 1L, "Juan Perez", "123456789", TipoPago.CONTADO,
                List.of(detalle), EstadoFactura.EMITIDA, BigDecimal.valueOf(3000), BigDecimal.ZERO,
                BigDecimal.valueOf(3000), 9L, "Ana Gomez", Instant.now(), null, 0, MedioPago.EFECTIVO);
        Pageable pageable = PageRequest.of(0, 10);
        when(clienteRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(cliente));
        when(facturaRepositoryPort.listarPorCliente(1L, pageable)).thenReturn(new PageImpl<>(List.of(factura)));

        Page<FacturaResult> resultado = service.ejecutar(1L, pageable);

        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).clienteId()).isEqualTo(1L);
    }

    @Test
    void ejecutar_conClienteInexistente_lanzaExcepcion() {
        Pageable pageable = PageRequest.of(0, 10);
        when(clienteRepositoryPort.buscarPorId(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.ejecutar(404L, pageable))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }
}
