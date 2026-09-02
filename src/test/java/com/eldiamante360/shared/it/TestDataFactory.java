package com.eldiamante360.shared.it;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Generador de datos unicos para las pruebas de integracion.
 *
 * <p>Como la suite no puede aislar datos con rollback transaccional (ver
 * {@link BaseIntegrationTest}), cada prueba que crea un recurso debe usar
 * un identificador/nombre unico para no colisionar con datos creados por
 * otras pruebas que comparten el mismo contenedor Postgres. Este factory
 * centraliza esa generacion para que sea consistente y facil de leer en
 * los tests (en vez de que cada clase invente su propio sufijo aleatorio).
 */
public final class TestDataFactory {

    // Contador atomico + nanoTime: unico incluso si varias pruebas corren
    // en paralelo dentro de la misma JVM (Failsafe puede paralelizar clases *IT).
    private static final AtomicLong SECUENCIA = new AtomicLong();

    private TestDataFactory() {
    }

    /** Sufijo alfanumerico corto y unico (base36) para nombres/usernames/documentos de prueba. */
    public static String sufijoUnico() {
        long valor = SECUENCIA.incrementAndGet() * 1_000_003L + System.nanoTime() % 1000;
        return Long.toString(Math.abs(valor), 36);
    }

    /** Username valido segun CrearUsuarioRequest (3-50 chars, [a-zA-Z0-9._-]). */
    public static String username(String prefijo) {
        return (prefijo + "_" + sufijoUnico()).toLowerCase();
    }

    /** Contrasena valida segun las reglas de CrearUsuarioRequest/CambiarPasswordRequest (>=8, letra+numero). */
    public static String passwordValida() {
        return "Passw0rd_" + sufijoUnico();
    }

    public static String nombreCompleto(String prefijo) {
        return prefijo + " " + sufijoUnico();
    }

    /** Numero de documento numerico unico (cedula/RUC de prueba), evita choque con UNIQUE constraints. */
    public static String numeroDocumento() {
        // Solo digitos, longitud estable, derivado del reloj + contador para unicidad.
        long base = 900_000_0000L + (Math.abs(SECUENCIA.incrementAndGet() * 7919L + System.nanoTime()) % 900_000_000L);
        return Long.toString(base);
    }

    public static String email(String prefijo) {
        return (prefijo + "." + sufijoUnico() + "@ittest.eldiamante360.local").toLowerCase();
    }

    public static String telefono() {
        long base = 3_000_000_000L + (Math.abs(System.nanoTime()) % 900_000_000L);
        return Long.toString(base);
    }

    /** Lista de un solo telefono de prueba, para CrearClienteRequest/ActualizarClienteRequest.telefonos(). */
    public static List<String> telefonos() {
        return List.of(telefono());
    }
}
