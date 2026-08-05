package com.eldiamante360.cliente.application.usecase;

import com.eldiamante360.cliente.application.dto.RutaResult;
import com.eldiamante360.cliente.application.port.RutaRepositoryPort;
import com.eldiamante360.cliente.domain.model.Ruta;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ObtenerRutaServiceTest {

    @Mock
    private RutaRepositoryPort rutaRepositoryPort;

    private ObtenerRutaService service;

    @BeforeEach
    void setUp() {
        service = new ObtenerRutaService(rutaRepositoryPort);
    }

    @Test
    void ejecutar_conRutaExistente_retornaLaRuta() {
        Ruta ruta = new Ruta(2L, "Ruta Norte", "Zona norte", true);
        when(rutaRepositoryPort.buscarPorId(2L)).thenReturn(Optional.of(ruta));

        RutaResult resultado = service.ejecutar(2L);

        assertThat(resultado.id()).isEqualTo(2L);
        assertThat(resultado.nombre()).isEqualTo("Ruta Norte");
    }

    @Test
    void ejecutar_conRutaInexistente_lanzaExcepcion() {
        when(rutaRepositoryPort.buscarPorId(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.ejecutar(404L))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }
}
