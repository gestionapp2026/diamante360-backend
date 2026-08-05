package com.eldiamante360.insumoquimico.application.usecase;

import com.eldiamante360.insumoquimico.application.dto.CrearInsumoQuimicoCommand;
import com.eldiamante360.insumoquimico.application.dto.InsumoQuimicoResult;
import com.eldiamante360.insumoquimico.application.port.InsumoQuimicoRepositoryPort;
import com.eldiamante360.insumoquimico.domain.exception.NombreInsumoQuimicoDuplicadoException;
import com.eldiamante360.insumoquimico.domain.model.InsumoQuimico;
import com.eldiamante360.insumoquimico.domain.model.UnidadMedidaInsumo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CrearInsumoQuimicoServiceTest {

    @Mock
    private InsumoQuimicoRepositoryPort insumoQuimicoRepositoryPort;

    private CrearInsumoQuimicoService service;

    @BeforeEach
    void setUp() {
        service = new CrearInsumoQuimicoService(insumoQuimicoRepositoryPort);
    }

    @Test
    void ejecutar_conDatosValidos_creaElInsumo() {
        CrearInsumoQuimicoCommand command = new CrearInsumoQuimicoCommand("Sal de cura", UnidadMedidaInsumo.KG);
        when(insumoQuimicoRepositoryPort.existePorNombre("Sal de cura")).thenReturn(false);
        when(insumoQuimicoRepositoryPort.guardar(any(InsumoQuimico.class))).thenAnswer(invocacion -> {
            InsumoQuimico insumo = invocacion.getArgument(0);
            return new InsumoQuimico(30L, insumo.getNombre(), insumo.getUnidadMedida(), insumo.getStockActual(),
                    insumo.isActivo(), 0, insumo.getPrecioCompra());
        });

        InsumoQuimicoResult resultado = service.ejecutar(command);

        assertThat(resultado.id()).isEqualTo(30L);
        assertThat(resultado.nombre()).isEqualTo("Sal de cura");
        assertThat(resultado.unidadMedida()).isEqualTo(UnidadMedidaInsumo.KG);
        assertThat(resultado.stockActual()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(resultado.activo()).isTrue();
    }

    @Test
    void ejecutar_conNombreYaExistente_lanzaExcepcion() {
        CrearInsumoQuimicoCommand command = new CrearInsumoQuimicoCommand("Sal de cura", UnidadMedidaInsumo.KG);
        when(insumoQuimicoRepositoryPort.existePorNombre("Sal de cura")).thenReturn(true);

        assertThatThrownBy(() -> service.ejecutar(command))
                .isInstanceOf(NombreInsumoQuimicoDuplicadoException.class);
    }
}
