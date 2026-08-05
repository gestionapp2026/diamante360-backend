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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ObtenerDependenciasRutaServiceTest {

    @Mock
    private RutaRepositoryPort rutaRepositoryPort;

    @Mock
    private ClienteJpaRepository clienteJpaRepository;

    private ObtenerDependenciasRutaService service;

    @BeforeEach
    void setUp() {
        service = new ObtenerDependenciasRutaService(rutaRepositoryPort, clienteJpaRepository);
    }

    @Test
    void ejecutar_conClientesAsignados_devuelveConteoSinBloqueo() {
        Ruta ruta = new Ruta(100L, "Ruta Norte", "Zona norte", true);
        when(rutaRepositoryPort.buscarPorId(100L)).thenReturn(Optional.of(ruta));
        when(clienteJpaRepository.countByRutaId(100L)).thenReturn(3L);

        var resultado = service.ejecutar(100L);

        assertThat(resultado.tieneDependencias()).isTrue();
        assertThat(resultado.bloqueado()).isFalse();
        assertThat(resultado.mensajeBloqueo()).isNull();
        assertThat(resultado.dependencias()).hasSize(1);
        assertThat(resultado.dependencias().get(0).cantidad()).isEqualTo(3L);
    }

    @Test
    void ejecutar_sinClientesAsignados_devuelveSinDependenciasNiBloqueo() {
        Ruta ruta = new Ruta(100L, "Ruta Norte", "Zona norte", true);
        when(rutaRepositoryPort.buscarPorId(100L)).thenReturn(Optional.of(ruta));
        when(clienteJpaRepository.countByRutaId(100L)).thenReturn(0L);

        var resultado = service.ejecutar(100L);

        assertThat(resultado.tieneDependencias()).isFalse();
        assertThat(resultado.bloqueado()).isFalse();
        assertThat(resultado.dependencias()).isEmpty();
    }

    @Test
    void ejecutar_conRutaInexistente_lanzaExcepcion() {
        when(rutaRepositoryPort.buscarPorId(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.ejecutar(404L))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }
}
