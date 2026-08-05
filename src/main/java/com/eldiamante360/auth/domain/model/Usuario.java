package com.eldiamante360.auth.domain.model;

import com.eldiamante360.auth.domain.exception.UsuarioInactivoException;

import java.time.Instant;

/**
 * Usuario del sistema. Contiene el hash de la contrasena (nunca la
 * contrasena en texto plano) y las reglas de negocio propias del usuario:
 * activacion/desactivacion, cambio de contrasena y control de acceso.
 */
public class Usuario {

    private final Long id;
    private String username;
    private String passwordHash;
    private String nombreCompleto;
    private Rol rol;
    private boolean activo;
    private boolean debeCambiarPassword;
    private Instant ultimoLogin;
    private final Integer version;

    public Usuario(Long id, String username, String passwordHash, String nombreCompleto,
                    Rol rol, boolean activo, boolean debeCambiarPassword, Instant ultimoLogin, Integer version) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.nombreCompleto = nombreCompleto;
        this.rol = rol;
        this.activo = activo;
        this.debeCambiarPassword = debeCambiarPassword;
        this.ultimoLogin = ultimoLogin;
        this.version = version;
    }

    public static Usuario nuevo(String username, String passwordHash, String nombreCompleto, Rol rol) {
        return new Usuario(null, username, passwordHash, nombreCompleto, rol, true, true, null, null);
    }

    public void verificarPuedeAutenticar() {
        if (!activo) {
            throw new UsuarioInactivoException();
        }
    }

    public void registrarLogin(Instant momento) {
        this.ultimoLogin = momento;
    }

    public void cambiarPassword(String nuevoHash) {
        this.passwordHash = nuevoHash;
        this.debeCambiarPassword = false;
    }

    /**
     * Restablece la contrasena de este usuario a una nueva (generada por un
     * administrador) y lo obliga a definir una propia en su siguiente login.
     */
    public void restablecerPassword(String nuevoHash) {
        this.passwordHash = nuevoHash;
        this.debeCambiarPassword = true;
    }

    public void activar() {
        this.activo = true;
    }

    public void desactivar() {
        this.activo = false;
    }

    public void actualizarDatos(String nombreCompleto, Rol rol) {
        this.nombreCompleto = nombreCompleto;
        this.rol = rol;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public Rol getRol() {
        return rol;
    }

    public boolean isActivo() {
        return activo;
    }

    public boolean isDebeCambiarPassword() {
        return debeCambiarPassword;
    }

    public Instant getUltimoLogin() {
        return ultimoLogin;
    }

    public Integer getVersion() {
        return version;
    }
}
