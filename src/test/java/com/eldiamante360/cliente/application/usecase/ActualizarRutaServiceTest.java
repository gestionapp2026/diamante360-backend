package com.eldiamante360.cliente.application.usecase;

import com.eldiamante360.cliente.application.dto.ActualizarRutaCommand;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActualizarRutaServiceTest {

    @Mock
    private RutaRepositoryPort rutaRepositoryPort;

    private ActualizarRutaService service;

    @BeforeEach
    void setUp() {
        service = new ActualizarRutaService(rutaRepositoryPort);
    }

    @Test
    void ejecutar_conDatosValidos_actualizaLaRuta() {
        Ruta ruta = new Ruta(2L, "Ruta Norte", "Zona norte", true);
        ActualizarRutaCommand command = new ActualizarRutaCommand(2L, "Ruta Norte Extendida", "Zona norte y noroccidente");
        when(rutaRepositoryPort.buscarPorId(2L)).thenReturn(Optional.of(ruta));
        when(rutaRepositoryPort.guardar(any(Ruta.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        RutaResult resultado = service.ejecutar(command);

        assertThat(resultado.nombre()).isEqualTo("Ruta Norte Extendida");
        assertThat(resultado.descripcion()).isEqualTo("Zona norte y noroccidente");
    }

    @Test
    void ejecutar_conRutaInexistente_lanzaExcepcion() {
        ActualizarRutaCommand command = new ActualizarRutaCommand(404L, "Ruta Norte", "Zona norte");
        when(rutaRepositoryPort.buscarPorId(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.ejecutar(command))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }
}
