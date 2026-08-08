package com.eldiamante360.shared.presentation;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

/**
 * Endpoint publico y liviano, sin autenticacion ni consultas a la base de
 * datos, pensado para servicios de monitoreo externos (ej. UptimeRobot) que
 * hacen ping periodico para evitar que Render duerma el servicio por
 * inactividad. A diferencia de /actuator/health, este no depende de que el
 * DataSource este saludable, asi que responde igual de rapido siempre.
 */
@RestController
@RequestMapping("/ping")
public class PingController {

    @GetMapping
    public Map<String, Object> ping() {
        return Map.of(
                "status", "ok",
                "timestamp", Instant.now().toString());
    }
}
