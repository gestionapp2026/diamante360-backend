package com.eldiamante360.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Encapsula la generacion y validacion de access tokens JWT (HS256). El
 * token embebe rol y permisos como claims para que JwtAuthenticationFilter
 * pueda autorizar cada request sin consultar la base de datos.
 */
@Component
public class JwtService {

    private static final String CLAIM_ROL = "rol";
    private static final String CLAIM_PERMISOS = "permisos";
    private static final String CLAIM_USUARIO_ID = "uid";

    private final JwtProperties jwtProperties;
    private final SecretKey key;

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public String generarAccessToken(Long usuarioId, String username, String rol, Set<String> permisos) {
        Instant ahora = Instant.now();
        Instant expiracion = ahora.plusSeconds(accessTokenExpiracionSegundos());

        return Jwts.builder()
                .subject(username)
                .claim(CLAIM_USUARIO_ID, usuarioId)
                .claim(CLAIM_ROL, rol)
                .claim(CLAIM_PERMISOS, permisos)
                .issuedAt(Date.from(ahora))
                .expiration(Date.from(expiracion))
                .signWith(key)
                .compact();
    }

    /**
     * Lanza io.jsonwebtoken.JwtException (o subclases como ExpiredJwtException,
     * SignatureException, MalformedJwtException) si el token no es valido.
     */
    public Claims validarYObtenerClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String obtenerUsername(Claims claims) {
        return claims.getSubject();
    }

    public Long obtenerUsuarioId(Claims claims) {
        return claims.get(CLAIM_USUARIO_ID, Long.class);
    }

    public String obtenerRol(Claims claims) {
        return claims.get(CLAIM_ROL, String.class);
    }

    @SuppressWarnings("unchecked")
    public List<String> obtenerPermisos(Claims claims) {
        return claims.get(CLAIM_PERMISOS, List.class);
    }

    public long accessTokenExpiracionSegundos() {
        return jwtProperties.getAccessTokenExpirationMinutes() * 60L;
    }

    public long refreshTokenExpiracionSegundos() {
        return jwtProperties.getRefreshTokenExpirationDays() * 24L * 60L * 60L;
    }
}
