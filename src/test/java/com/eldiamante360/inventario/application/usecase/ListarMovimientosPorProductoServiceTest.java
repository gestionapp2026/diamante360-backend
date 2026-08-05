package com.eldiamante360.inventario.application.usecase;

import com.eldiamante360.inventario.application.dto.MovimientoResult;
import com.eldiamante360.inventario.application.port.MovimientoInventarioRepositoryPort;
import com.eldiamante360.inventario.domain.model.MovimientoInventario;
import com.eldiamante360.inventario.domain.model.TipoMovimiento;
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
class ListarMovimientosPorProductoServiceTest {

    @Mock
    private MovimientoInventarioRepositoryPort movimientoInventarioRepositoryPort;

    private ListarMovimientosPorProductoService service;

    @BeforeEach
    void setUp() {
        service = new ListarMovimientosPorProductoService(movimientoInventarioRepositoryPort);
    }

    @Test
    void ejecutar_retornaLaPaginaMapeada() {
        MovimientoInventario movimiento = new MovimientoInventario(1L, 5L, TipoMovimiento.ENTRADA,
                BigDecimal.valueOf(5), BigDecimal.valueOf(15), "Compra", 1L, Instant.now());
        Pageable pageable = PageRequest.of(0, 10);
        Page<MovimientoInventario> pagina = new PageImpl<>(List.of(movimiento));
        when(movimientoInventarioRepositoryPort.listarPorProducto(5L, pageable)).thenReturn(pagina);

        Page<MovimientoResult> resultado = service.ejecutar(5L, pageable);

        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).productoId()).isEqualTo(5L);
    }
}
