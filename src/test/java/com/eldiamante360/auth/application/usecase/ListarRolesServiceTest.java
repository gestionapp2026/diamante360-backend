package com.eldiamante360.auth.application.usecase;

import com.eldiamante360.auth.application.port.RolRepositoryPort;
import com.eldiamante360.auth.domain.model.Rol;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListarRolesServiceTest {

    @Mock
    private RolRepositoryPort rolRepositoryPort;

    private ListarRolesService service;

    @BeforeEach
    void setUp() {
        service = new ListarRolesService(rolRepositoryPort);
    }

    @Test
    void ejecutar_retornaTodosLosRolesDelRepositorio() {
        List<Rol> roles = List.of(
                new Rol(1L, "ADMIN", "Administrador", Set.of()),
                new Rol(2L, "VENDEDOR", "Vendedor", Set.of())
        );
        when(rolRepositoryPort.listarTodos()).thenReturn(roles);

        List<Rol> resultado = service.ejecutar();

        assertThat(resultado).hasSize(2).containsExactlyElementsOf(roles);
    }
}
