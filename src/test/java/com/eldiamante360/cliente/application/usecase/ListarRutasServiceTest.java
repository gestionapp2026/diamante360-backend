package com.eldiamante360.cliente.application.usecase;

import com.eldiamante360.cliente.application.dto.RutaResult;
import com.eldiamante360.cliente.application.port.RutaRepositoryPort;
import com.eldiamante360.cliente.domain.model.Ruta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListarRutasServiceTest {

    @Mock
    private RutaRepositoryPort rutaRepositoryPort;

    private ListarRutasService service;

    @BeforeEach
    void setUp() {
        service = new ListarRutasService(rutaRepositoryPort);
    }

    @Test
    void ejecutar_retornaTodasLasRutasMapeadas() {
        Ruta ruta = new Ruta(2L, "Ruta Norte", "Zona norte", true);
        when(rutaRepositoryPort.listarTodas()).thenReturn(List.of(ruta));

        List<RutaResult> resultado = service.ejecutar();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).nombre()).isEqualTo("Ruta Norte");
    }
}
