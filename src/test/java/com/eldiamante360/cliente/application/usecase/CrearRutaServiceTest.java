package com.eldiamante360.cliente.application.usecase;

import com.eldiamante360.cliente.application.dto.CrearRutaCommand;
import com.eldiamante360.cliente.application.dto.RutaResult;
import com.eldiamante360.cliente.application.port.RutaRepositoryPort;
import com.eldiamante360.cliente.domain.exception.NombreRutaDuplicadaException;
import com.eldiamante360.cliente.domain.model.Ruta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CrearRutaServiceTest {

    @Mock
    private RutaRepositoryPort rutaRepositoryPort;

    private CrearRutaService service;

    @BeforeEach
    void setUp() {
        service = new CrearRutaService(rutaRepositoryPort);
    }

    @Test
    void ejecutar_conNombreLibre_creaLaRuta() {
        CrearRutaCommand command = new CrearRutaCommand("Ruta Norte", "Zona norte");
        when(rutaRepositoryPort.existePorNombre("Ruta Norte")).thenReturn(false);
        when(rutaRepositoryPort.guardar(any(Ruta.class))).thenAnswer(invocacion -> {
            Ruta r = invocacion.getArgument(0);
            return new Ruta(1L, r.getNombre(), r.getDescripcion(), r.isActivo());
        });

        RutaResult resultado = service.ejecutar(command);

        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.nombre()).isEqualTo("Ruta Norte");
        assertThat(resultado.activo()).isTrue();
    }

    @Test
    void ejecutar_conNombreYaExistente_lanzaExcepcion() {
        CrearRutaCommand command = new CrearRutaCommand("Ruta Norte", "Zona norte");
        when(rutaRepositoryPort.existePorNombre("Ruta Norte")).thenReturn(true);

        assertThatThrownBy(() -> service.ejecutar(command))
                .isInstanceOf(NombreRutaDuplicadaException.class);
    }
}
