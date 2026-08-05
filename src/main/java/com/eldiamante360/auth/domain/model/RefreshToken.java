package com.eldiamante360.auth.domain.model;

import java.time.Instant;

/**
 * Refresh token opaco (no JWT). Se persiste unicamente su hash SHA-256;
 * el valor en texto plano solo existe en memoria durante la emision y se
 * entrega una unica vez al cliente.
 */
public class RefreshToken {

    private final Long id;
    private final Long usuarioId;
    private final String tokenHash;
    private final Instant fechaExpiracion;
    private boolean revocado;
    private Instant fechaRevocacion;
    private final String ipOrigen;
    private final String userAgent;
    private final Instant createdAt;

    public RefreshToken(Long id, Long usuarioId, String tokenHash, Instant fechaExpiracion,
                         boolean revocado, Instant fechaRevocacion, String ipOrigen, String userAgent, Instant createdAt) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.tokenHash = tokenHash;
        this.fechaExpiracion = fechaExpiracion;
        this.revocado = revocado;
        this.fechaRevocacion = fechaRevocacion;
        this.ipOrigen = ipOrigen;
        this.userAgent = userAgent;
        this.createdAt = createdAt;
    }

    public static RefreshToken nuevo(Long usuarioId, String tokenHash, Instant fechaExpiracion, String ipOrigen, String userAgent) {
        return new RefreshToken(null, usuarioId, tokenHash, fechaExpiracion, false, null, ipOrigen, userAgent, Instant.now());
    }

    public boolean estaVigente(Instant ahora) {
        return !revocado && fechaExpiracion.isAfter(ahora);
    }

    public void revocar(Instant momento) {
        this.revocado = true;
        this.fechaRevocacion = momento;
    }

    public Long getId() {
        return id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public Instant getFechaExpiracion() {
        return fechaExpiracion;
    }

    public boolean isRevocado() {
        return revocado;
    }

    public Instant getFechaRevocacion() {
        return fechaRevocacion;
    }

    public String getIpOrigen() {
        return ipOrigen;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
