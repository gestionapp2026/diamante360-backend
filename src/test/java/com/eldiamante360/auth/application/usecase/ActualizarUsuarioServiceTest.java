package com.eldiamante360.auth.application.usecase;

import com.eldiamante360.auth.application.dto.ActualizarUsuarioCommand;
import com.eldiamante360.auth.application.dto.UsuarioResult;
import com.eldiamante360.auth.application.port.RolRepositoryPort;
import com.eldiamante360.auth.application.port.UsuarioRepositoryPort;
import com.eldiamante360.auth.domain.exception.NombreUsuarioDuplicadoException;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActualizarUsuarioServiceTest {

    @Mock
    private UsuarioRepositoryPort usuarioRepositoryPort;

    @Mock
    private RolRepositoryPort rolRepositoryPort;

    private ActualizarUsuarioService service;

    private Rol rolAdmin;
    private Rol rolVendedor;

    @BeforeEach
    void setUp() {
        service = new ActualizarUsuarioService(usuarioRepositoryPort, rolRepositoryPort);
        rolAdmin = new Rol(1L, "ADMIN", "Administrador", Set.of());
        rolVendedor = new Rol(2L, "VENDEDOR", "Vendedor", Set.of());
    }

    @Test
    void ejecutar_actualizaNombreYRolDelUsuarioExistente() {
        Usuario usuario = new Usuario(5L, "jhon", "hash", "Jhon Perez", rolAdmin, true, false, null, 0);
        ActualizarUsuarioCommand command = new ActualizarUsuarioCommand(5L, "jhon", "Jhon Alberto Perez", 2L);

        when(usuarioRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(usuario));
        when(rolRepositoryPort.buscarPorId(2L)).thenReturn(Optional.of(rolVendedor));
        when(usuarioRepositoryPort.guardar(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        UsuarioResult resultado = service.ejecutar(command);

        assertThat(resultado.nombreCompleto()).isEqualTo("Jhon Alberto Perez");
        assertThat(resultado.rolNombre()).isEqualTo("VENDEDOR");
    }

    @Test
    void ejecutar_cambiaElUsername() {
        Usuario usuario = new Usuario(5L, "jhon", "hash", "Jhon Perez", rolAdmin, true, false, null, 0);
        ActualizarUsuarioCommand command = new ActualizarUsuarioCommand(5L, "jhon.perez", "Jhon Perez", 1L);

        when(usuarioRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(usuario));
        when(usuarioRepositoryPort.existePorUsername("jhon.perez")).thenReturn(false);
        when(rolRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(rolAdmin));
        when(usuarioRepositoryPort.guardar(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        UsuarioResult resultado = service.ejecutar(command);

        assertThat(resultado.username()).isEqualTo("jhon.perez");
    }

    @Test
    void ejecutar_conUsernameYaUsadoPorOtroUsuario_lanzaExcepcion() {
        Usuario usuario = new Usuario(5L, "jhon", "hash", "Jhon Perez", rolAdmin, true, false, null, 0);
        ActualizarUsuarioCommand command = new ActualizarUsuarioCommand(5L, "angie", "Jhon Perez", 1L);

        when(usuarioRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(usuario));
        when(usuarioRepositoryPort.existePorUsername("angie")).thenReturn(true);

        assertThatThrownBy(() -> service.ejecutar(command))
                .isInstanceOf(NombreUsuarioDuplicadoException.class);
    }

    @Test
    void ejecutar_conUsuarioInexistente_lanzaExcepcion() {
        ActualizarUsuarioCommand command = new ActualizarUsuarioCommand(404L, "jhon", "Nombre", 1L);
        when(usuarioRepositoryPort.buscarPorId(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.ejecutar(command))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    void ejecutar_conRolInexistente_lanzaExcepcion() {
        Usuario usuario = new Usuario(5L, "jhon", "hash", "Jhon Perez", rolAdmin, true, false, null, 0);
        ActualizarUsuarioCommand command = new ActualizarUsuarioCommand(5L, "jhon", "Jhon Perez", 99L);

        when(usuarioRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(usuario));
        when(rolRepositoryPort.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.ejecutar(command))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }
}
