package com.eldiamante360.auth.infrastructure;

import com.eldiamante360.auth.application.port.PasswordEncoderPort;
import com.eldiamante360.auth.application.port.RolRepositoryPort;
import com.eldiamante360.auth.application.port.UsuarioRepositoryPort;
import com.eldiamante360.auth.domain.model.Rol;
import com.eldiamante360.auth.domain.model.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Crea los usuarios administradores iniciales (Jhon, Angie) en el primer
 * arranque, si aun no existen. La contrasena se hashea con el
 * PasswordEncoder real (BCrypt) en vez de calcular un hash a mano y
 * pegarlo en un script SQL; queda marcada como "debe cambiar contrasena"
 * para forzar su rotacion en el primer login.
 */
@Component
public class AdminUserSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminUserSeeder.class);
    private static final List<String> ADMINISTRADORES = List.of("jhon", "angie");
    private static final String ROL_ADMIN = "ADMIN";

    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final RolRepositoryPort rolRepositoryPort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final String passwordPorDefecto;

    public AdminUserSeeder(UsuarioRepositoryPort usuarioRepositoryPort,
                            RolRepositoryPort rolRepositoryPort,
                            PasswordEncoderPort passwordEncoderPort,
                            @Value("${app.admin.default-password:ElDiamante360!Temporal}") String passwordPorDefecto) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.rolRepositoryPort = rolRepositoryPort;
        this.passwordEncoderPort = passwordEncoderPort;
        this.passwordPorDefecto = passwordPorDefecto;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        Rol rolAdmin = rolRepositoryPort.buscarPorNombre(ROL_ADMIN)
                .orElseThrow(() -> new IllegalStateException(
                        "El rol ADMIN no existe; verifique que la migracion V1__create_seguridad.sql se aplico"));

        for (String username : ADMINISTRADORES) {
            if (usuarioRepositoryPort.existePorUsername(username)) {
                continue;
            }
            String hash = passwordEncoderPort.encode(passwordPorDefecto);
            Usuario nuevo = Usuario.nuevo(username, hash, capitalizar(username), rolAdmin);
            usuarioRepositoryPort.guardar(nuevo);
            log.warn("Usuario administrador '{}' creado con contrasena temporal; debe cambiarla en el primer login", username);
        }
    }

    private String capitalizar(String username) {
        return Character.toUpperCase(username.charAt(0)) + username.substring(1);
    }
}
