package com.eldiamante360.auth.application.usecase;

import java.security.SecureRandom;

/**
 * Genera contrasenas temporales legibles (letras + digitos) para el flujo de
 * restablecimiento por parte de un administrador. Garantiza al menos una
 * letra y un digito para cumplir la misma validacion que se exige al crear
 * o cambiar una contrasena (ver CrearUsuarioRequest/CambiarPasswordRequest).
 */
final class GeneradorPasswordTemporal {

    private static final String LETRAS = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz";
    private static final String DIGITOS = "23456789";
    private static final String TODOS = LETRAS + DIGITOS;
    private static final int LONGITUD = 10;
    private static final SecureRandom RANDOM = new SecureRandom();

    private GeneradorPasswordTemporal() {
    }

    static String generar() {
        StringBuilder sb = new StringBuilder(LONGITUD);
        sb.append(LETRAS.charAt(RANDOM.nextInt(LETRAS.length())));
        sb.append(DIGITOS.charAt(RANDOM.nextInt(DIGITOS.length())));
        for (int i = 2; i < LONGITUD; i++) {
            sb.append(TODOS.charAt(RANDOM.nextInt(TODOS.length())));
        }
        return mezclar(sb.toString());
    }

    private static String mezclar(String texto) {
        char[] caracteres = texto.toCharArray();
        for (int i = caracteres.length - 1; i > 0; i--) {
            int j = RANDOM.nextInt(i + 1);
            char temp = caracteres[i];
            caracteres[i] = caracteres[j];
            caracteres[j] = temp;
        }
        return new String(caracteres);
    }
}
