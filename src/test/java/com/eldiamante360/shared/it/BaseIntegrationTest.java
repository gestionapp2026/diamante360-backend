package com.eldiamante360.shared.it;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

/**
 * Clase base de todas las pruebas de integracion de la API REST (clases
 * *IT, ejecutadas via {@code mvn verify}).
 *
 * <p>Levanta un servidor Spring Boot real en un puerto aleatorio
 * ({@code webEnvironment = RANDOM_PORT}) contra una base de datos
 * PostgreSQL real gestionada por Testcontainers, y configura RestAssured
 * para hacer peticiones HTTP reales contra ese servidor. Esto permite
 * ejercitar el pipeline completo: filtros de seguridad, JWT, serializacion
 * JSON, validaciones y manejo de errores, tal como lo haria un cliente
 * real (a diferencia de MockMvc, que no pasa por un socket HTTP real).
 *
 * <p><b>Aislamiento de datos:</b> a diferencia de las pruebas unitarias,
 * estas pruebas NO usan rollback transaccional (@Transactional en el
 * metodo de test no funciona aqui: la peticion HTTP la procesa un hilo
 * del servidor con su propia transaccion, distinta a la del hilo de
 * test). El contenedor Postgres se comparte entre todas las clases *IT
 * de la suite (mismo contexto de Spring, cacheado), por lo que cada
 * prueba debe generar sus propios datos con identificadores unicos
 * (ver {@link TestDataFactory}) en vez de asumir un estado inicial vacio
 * o contar filas totales en endpoints de listado compartidos.
 */
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseIntegrationTest {

    /** Secreto HS256 (>= 32 caracteres) usado unicamente en las pruebas de integracion. */
    protected static final String JWT_SECRET_TEST =
            "el-diamante-360-integration-tests-jwt-secret-key-0123456789";

    /** Contrasena por defecto de los administradores sembrados por AdminUserSeeder en este entorno de pruebas. */
    protected static final String ADMIN_DEFAULT_PASSWORD_TEST = "ElDiamante360!Temporal";

    @Container
    static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"))
                    .withDatabaseName("eldiamante360_it")
                    .withUsername("eldiamante360_it")
                    .withPassword("eldiamante360_it");

    @DynamicPropertySource
    static void propiedadesDinamicas(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("app.jwt.secret", () -> JWT_SECRET_TEST);
        registry.add("app.admin.default-password", () -> ADMIN_DEFAULT_PASSWORD_TEST);
    }

    @LocalServerPort
    private int puertoLocal;

    @BeforeEach
    void configurarRestAssured() {
        RestAssured.port = puertoLocal;
        RestAssured.basePath = "/api/v1";
    }
}
