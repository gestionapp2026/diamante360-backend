package com.eldiamante360.deudor.application.usecase;

import com.eldiamante360.deudor.application.dto.AbonoResult;
import com.eldiamante360.deudor.application.port.AbonoRepositoryPort;
import com.eldiamante360.deudor.application.port.CuentaPorCobrarRepositoryPort;
import com.eldiamante360.deudor.domain.model.Abono;
import com.eldiamante360.deudor.domain.model.CuentaPorCobrar;
import com.eldiamante360.deudor.domain.model.EstadoCuentaPorCobrar;
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
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListarAbonosServiceTest {

    @Mock
    private AbonoRepositoryPort abonoRepositoryPort;

    @Mock
    private CuentaPorCobrarRepositoryPort cuentaPorCobrarRepositoryPort;

    private ListarAbonosService service;

    @BeforeEach
    void setUp() {
        service = new ListarAbonosService(abonoRepositoryPort, cuentaPorCobrarRepositoryPort);
    }

    private CuentaPorCobrar cuentaPendiente(Long id) {
        return new CuentaPorCobrar(id, 100L, "FAC-2026-00001", 1L, "Juan Perez", "123456789",
                BigDecimal.valueOf(1000), BigDecimal.valueOf(600), EstadoCuentaPorCobrar.PARCIAL, 9L, null, null,
                null, 0);
    }

    @Test
    void ejecutar_conCuentaExistente_retornaLaPaginaMapeada() {
        Abono abono = new Abono(1L, 50L, BigDecimal.valueOf(400), 9L, Instant.now(), MedioPago.EFECTIVO);
        Pageable pageable = PageRequest.of(0, 10);
        when(cuentaPorCobrarRepositoryPort.buscarPorId(50L)).thenReturn(Optional.of(cuentaPendiente(50L)));
        when(abonoRepositoryPort.listarPorCuenta(50L, pageable)).thenReturn(new PageImpl<>(List.of(abono)));

        Page<AbonoResult> resultado = service.ejecutar(50L, pageable);

        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).monto()).isEqualByComparingTo(BigDecimal.valueOf(400));
    }

    @Test
    void ejecutar_conCuentaInexistente_lanzaExcepcionYNoInteractuaConAbonos() {
        Pageable pageable = PageRequest.of(0, 10);
        when(cuentaPorCobrarRepositoryPort.buscarPorId(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.ejecutar(404L, pageable))
                .isInstanceOf(RecursoNoEncontradoException.class);

        verifyNoInteractions(abonoRepositoryPort);
    }
}
