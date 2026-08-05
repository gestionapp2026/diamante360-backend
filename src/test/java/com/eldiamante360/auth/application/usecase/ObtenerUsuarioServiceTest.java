package com.eldiamante360.auth.application.usecase;

import com.eldiamante360.auth.application.dto.UsuarioResult;
import com.eldiamante360.auth.application.port.UsuarioRepositoryPort;
import com.eldiamante360.auth.domain.model.Rol;
import com.eldiamante360.auth.domain.model.Usuario;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ObtenerUsuarioServiceTest {

    @Mock
    private UsuarioRepositoryPort usuarioRepositoryPort;

    private ObtenerUsuarioService service;

    @BeforeEach
    void setUp() {
        service = new ObtenerUsuarioService(usuarioRepositoryPort);
    }

    @Test
    void ejecutar_conIdExistente_retornaElUsuario() {
        Rol rolAdmin = new Rol(1L, "ADMIN", "Administrador", Set.of());
        Usuario usuario = new Usuario(5L, "jhon", "hash", "Jhon Perez", rolAdmin, true, false, null, 0);
        when(usuarioRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(usuario));

        UsuarioResult resultado = service.ejecutar(5L);

        assertThat(resultado.id()).isEqualTo(5L);
        assertThat(resultado.username()).isEqualTo("jhon");
    }

    @Test
    void ejecutar_conIdInexistente_lanzaExcepcion() {
        when(usuarioRepositoryPort.buscarPorId(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.ejecutar(404L))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }
}
