package com.eldiamante360.auth.domain.model;

import com.eldiamante360.auth.domain.exception.UsuarioInactivoException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UsuarioTest {

    private final Rol rolAdmin = new Rol(1L, "ADMIN", "Administrador", Set.of(
            new Permiso(1L, "USUARIO_CREAR", "Crear usuarios", "USUARIO")
    ));

    @Test
    void nuevo_creaUsuarioActivoQueDebeCambiarPassword() {
        Usuario usuario = Usuario.nuevo("jhon", "hash", "Jhon Perez", rolAdmin);

        assertThat(usuario.isActivo()).isTrue();
        assertThat(usuario.isDebeCambiarPassword()).isTrue();
        assertThat(usuario.getId()).isNull();
        assertThat(usuario.getUltimoLogin()).isNull();
    }

    @Test
    void verificarPuedeAutenticar_usuarioActivo_noLanzaExcepcion() {
        Usuario usuario = Usuario.nuevo("jhon", "hash", "Jhon Perez", rolAdmin);

        assertThatCode(usuario::verificarPuedeAutenticar).doesNotThrowAnyException();
    }

    @Test
    void verificarPuedeAutenticar_usuarioInactivo_lanzaExcepcion() {
        Usuario usuario = Usuario.nuevo("jhon", "hash", "Jhon Perez", rolAdmin);
        usuario.desactivar();

        assertThatThrownBy(usuario::verificarPuedeAutenticar)
                .isInstanceOf(UsuarioInactivoException.class);
    }

    @Test
    void registrarLogin_actualizaUltimoLogin() {
        Usuario usuario = Usuario.nuevo("jhon", "hash", "Jhon Perez", rolAdmin);
        Instant ahora = Instant.now();

        usuario.registrarLogin(ahora);

        assertThat(usuario.getUltimoLogin()).isEqualTo(ahora);
    }

    @Test
    void cambiarPassword_actualizaHashYLimpiaFlagDeCambioObligatorio() {
        Usuario usuario = Usuario.nuevo("jhon", "hashViejo", "Jhon Perez", rolAdmin);

        usuario.cambiarPassword("hashNuevo");

        assertThat(usuario.getPasswordHash()).isEqualTo("hashNuevo");
        assertThat(usuario.isDebeCambiarPassword()).isFalse();
    }

    @Test
    void activarYDesactivar_cambianElEstado() {
        Usuario usuario = Usuario.nuevo("jhon", "hash", "Jhon Perez", rolAdmin);

        usuario.desactivar();
        assertThat(usuario.isActivo()).isFalse();

        usuario.activar();
        assertThat(usuario.isActivo()).isTrue();
    }

    @Test
    void actualizarDatos_cambiaNombreYRol() {
        Usuario usuario = Usuario.nuevo("jhon", "hash", "Jhon Perez", rolAdmin);
        Rol rolVendedor = new Rol(2L, "VENDEDOR", "Vendedor", Set.of());

        usuario.actualizarDatos("Jhon Alberto Perez", rolVendedor);

        assertThat(usuario.getNombreCompleto()).isEqualTo("Jhon Alberto Perez");
        assertThat(usuario.getRol()).isEqualTo(rolVendedor);
    }
}
