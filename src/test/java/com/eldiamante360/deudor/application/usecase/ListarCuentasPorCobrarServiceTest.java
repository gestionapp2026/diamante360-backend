package com.eldiamante360.deudor.application.usecase;

import com.eldiamante360.deudor.application.dto.CuentaPorCobrarResult;
import com.eldiamante360.deudor.application.port.CuentaPorCobrarRepositoryPort;
import com.eldiamante360.deudor.domain.model.CuentaPorCobrar;
import com.eldiamante360.deudor.domain.model.EstadoCuentaPorCobrar;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListarCuentasPorCobrarServiceTest {

    @Mock
    private CuentaPorCobrarRepositoryPort cuentaPorCobrarRepositoryPort;

    private ListarCuentasPorCobrarService service;

    @BeforeEach
    void setUp() {
        service = new ListarCuentasPorCobrarService(cuentaPorCobrarRepositoryPort);
    }

    @Test
    void ejecutar_retornaLaPaginaMapeada() {
        CuentaPorCobrar cuenta = new CuentaPorCobrar(50L, 100L, "FAC-2026-00001", 1L, "Juan Perez", "123456789",
                BigDecimal.valueOf(1000), BigDecimal.valueOf(1000), EstadoCuentaPorCobrar.PENDIENTE, 9L, null, null,
                null, 0);
        Pageable pageable = PageRequest.of(0, 10);
        when(cuentaPorCobrarRepositoryPort.listar(pageable)).thenReturn(new PageImpl<>(List.of(cuenta)));

        Page<CuentaPorCobrarResult> resultado = service.ejecutar(pageable);

        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).id()).isEqualTo(50L);
    }
}
