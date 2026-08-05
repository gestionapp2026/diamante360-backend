package com.eldiamante360.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Documentacion de la API expuesta via springdoc-openapi: JSON en
 * /v3/api-docs y Swagger UI en /swagger-ui.html (con el context-path
 * configurado, queda en /api/v1/swagger-ui.html). Se declara el esquema
 * de seguridad Bearer (JWT) para poder autenticarse desde el boton
 * "Authorize" de Swagger UI y probar los endpoints protegidos.
 */
@Configuration
public class OpenApiConfig {

    private static final String ESQUEMA_BEARER = "bearerAuth";

    @Bean
    public OpenAPI elDiamante360OpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("El Diamante 360 - API")
                        .description("API REST del ERP de El Diamante 360 (produccion y venta de productos carnicos): "
                                + "seguridad, clientes, productos e inventario, insumos quimicos, facturacion y deudores.")
                        .version("v1")
                        .contact(new Contact().name("El Diamante 360")))
                .addSecurityItem(new SecurityRequirement().addList(ESQUEMA_BEARER))
                .components(new Components()
                        .addSecuritySchemes(ESQUEMA_BEARER, new SecurityScheme()
                                .name(ESQUEMA_BEARER)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
