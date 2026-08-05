package com.eldiamante360.cliente.application.usecase;

import com.eldiamante360.cliente.application.port.RutaRepositoryPort;
import com.eldiamante360.cliente.domain.model.Ruta;
import com.eldiamante360.cliente.infrastructure.persistence.repository.ClienteJpaRepository;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EliminarRutaServiceTest {

    @Mock
    private RutaRepositoryPort rutaRepositoryPort;

    @Mock
    private ClienteJpaRepository clienteJpaRepository;

    private EliminarRutaService service;

    @BeforeEach
    void setUp() {
        service = new EliminarRutaService(rutaRepositoryPort, clienteJpaRepository);
    }

    @Test
    void ejecutar_conRutaExistente_desasignaClientesYLaElimina() {
        Ruta ruta = new Ruta(100L, "Ruta Norte", "Zona norte", true);
        when(rutaRepositoryPort.buscarPorId(100L)).thenReturn(Optional.of(ruta));

        service.ejecutar(100L, false);

        verify(clienteJpaRepository).desasignarRuta(100L);
        verify(rutaRepositoryPort).eliminar(100L);
    }

    @Test
    void ejecutar_conCascadaTrue_tambienDesasignaClientesYLaElimina() {
        Ruta ruta = new Ruta(100L, "Ruta Norte", "Zona norte", true);
        when(rutaRepositoryPort.buscarPorId(100L)).thenReturn(Optional.of(ruta));

        service.ejecutar(100L, true);

        verify(clienteJpaRepository).desasignarRuta(100L);
        verify(rutaRepositoryPort).eliminar(100L);
    }

    @Test
    void ejecutar_conRutaInexistente_lanzaExcepcion() {
        when(rutaRepositoryPort.buscarPorId(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.ejecutar(404L, false))
                .isInstanceOf(RecursoNoEncontradoException.class);

        verify(clienteJpaRepository, never()).desasignarRuta(404L);
        verify(rutaRepositoryPort, never()).eliminar(404L);
    }
}
