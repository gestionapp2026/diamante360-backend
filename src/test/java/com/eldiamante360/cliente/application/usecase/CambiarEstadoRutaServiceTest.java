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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CambiarEstadoRutaServiceTest {

    @Mock
    private RutaRepositoryPort rutaRepositoryPort;

    private CambiarEstadoRutaService service;

    @Test
    void ejecutar_conActivoFalso_desactivaLaRuta() {
        service = new CambiarEstadoRutaService(rutaRepositoryPort);
        Ruta ruta = new Ruta(2L, "Ruta Norte", "Zona norte", true);
        when(rutaRepositoryPort.buscarPorId(2L)).thenReturn(Optional.of(ruta));
        when(rutaRepositoryPort.guardar(any(Ruta.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        RutaResult resultado = service.ejecutar(2L, false);

        assertThat(resultado.activo()).isFalse();
    }

    @Test
    void ejecutar_conIdInexistente_lanzaExcepcion() {
        service = new CambiarEstadoRutaService(rutaRepositoryPort);
        when(rutaRepositoryPort.buscarPorId(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.ejecutar(404L, true))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }
}
