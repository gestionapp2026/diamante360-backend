package com.eldiamante360.auth.application.usecase;

import com.eldiamante360.auth.application.dto.CrearUsuarioCommand;
import com.eldiamante360.auth.application.dto.UsuarioResult;
import com.eldiamante360.auth.application.port.PasswordEncoderPort;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CrearUsuarioServiceTest {

    @Mock
    private UsuarioRepositoryPort usuarioRepositoryPort;

    @Mock
    private RolRepositoryPort rolRepositoryPort;

    @Mock
    private PasswordEncoderPort passwordEncoderPort;

    private CrearUsuarioService service;

    private Rol rolAdmin;

    @BeforeEach
    void setUp() {
        service = new CrearUsuarioService(usuarioRepositoryPort, rolRepositoryPort, passwordEncoderPort);
        rolAdmin = new Rol(1L, "ADMIN", "Administrador", Set.of());
    }

    @Test
    void ejecutar_conUsernameLibre_creaUsuarioConPasswordHasheado() {
        CrearUsuarioCommand command = new CrearUsuarioCommand("jhon", "Secreta123", "Jhon Perez", 1L);
        when(usuarioRepositoryPort.existePorUsername("jhon")).thenReturn(false);
        when(rolRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(rolAdmin));
        when(passwordEncoderPort.encode("Secreta123")).thenReturn("hash-bcrypt");
        when(usuarioRepositoryPort.guardar(any(Usuario.class))).thenAnswer(invocacion -> {
            Usuario u = invocacion.getArgument(0);
            return new Usuario(10L, u.getUsername(), u.getPasswordHash(), u.getNombreCompleto(),
                    u.getRol(), u.isActivo(), u.isDebeCambiarPassword(), u.getUltimoLogin(), 0);
        });

        UsuarioResult resultado = service.ejecutar(command);

        assertThat(resultado.id()).isEqualTo(10L);
        assertThat(resultado.username()).isEqualTo("jhon");
        assertThat(resultado.rolNombre()).isEqualTo("ADMIN");
        verify(passwordEncoderPort).encode("Secreta123");
    }

    @Test
    void ejecutar_conUsernameYaExistente_lanzaExcepcion() {
        CrearUsuarioCommand command = new CrearUsuarioCommand("jhon", "Secreta123", "Jhon Perez", 1L);
        when(usuarioRepositoryPort.existePorUsername("jhon")).thenReturn(true);

        assertThatThrownBy(() -> service.ejecutar(command))
                .isInstanceOf(NombreUsuarioDuplicadoException.class);

        verifyNoInteractions(passwordEncoderPort);
    }

    @Test
    void ejecutar_conRolInexistente_lanzaExcepcion() {
        CrearUsuarioCommand command = new CrearUsuarioCommand("jhon", "Secreta123", "Jhon Perez", 99L);
        when(usuarioRepositoryPort.existePorUsername("jhon")).thenReturn(false);
        when(rolRepositoryPort.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.ejecutar(command))
                .isInstanceOf(RecursoNoEncontradoException.class);

        verifyNoInteractions(passwordEncoderPort);
    }
}
