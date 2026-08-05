package com.eldiamante360.auth.application.port;

import com.eldiamante360.auth.domain.model.Usuario;

/**
 * Puerto para emision de tokens. El access token es un JWT firmado que
 * embebe rol y permisos como claims (evita ir a BD en cada request). El
 * refresh token es un valor opaco aleatorio; solo su hash SHA-256 se
 * persiste.
 */
public interface TokenServicePort {

    String generarAccessToken(Usuario usuario);

    long accessTokenExpiracionSegundos();

    String generarRefreshTokenOpaco();

    String hashRefreshToken(String tokenPlano);

    long refreshTokenExpiracionSegundos();
}
