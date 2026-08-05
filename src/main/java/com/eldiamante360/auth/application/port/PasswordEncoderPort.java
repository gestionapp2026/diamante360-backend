package com.eldiamante360.auth.application.port;

/**
 * Abstrae el algoritmo de hashing de contrasenas (BCrypt) para que la capa
 * de aplicacion no dependa de Spring Security directamente.
 */
public interface PasswordEncoderPort {

    String encode(String passwordPlano);

    boolean matches(String passwordPlano, String passwordHash);
}
