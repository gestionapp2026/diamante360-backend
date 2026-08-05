package com.eldiamante360.auth.infrastructure;

import com.eldiamante360.auth.application.port.TokenServicePort;
import com.eldiamante360.auth.domain.model.Usuario;
import com.eldiamante360.security.jwt.JwtService;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;

@Component
public class JwtTokenServiceAdapter implements TokenServicePort {

    private static final int REFRESH_TOKEN_BYTES = 64;

    private final JwtService jwtService;
    private final SecureRandom secureRandom = new SecureRandom();

    public JwtTokenServiceAdapter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public String generarAccessToken(Usuario usuario) {
        return jwtService.generarAccessToken(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getRol().nombre(),
                usuario.getRol().codigosPermisos());
    }

    @Override
    public long accessTokenExpiracionSegundos() {
        return jwtService.accessTokenExpiracionSegundos();
    }

    @Override
    public String generarRefreshTokenOpaco() {
        byte[] bytes = new byte[REFRESH_TOKEN_BYTES];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    @Override
    public String hashRefreshToken(String tokenPlano) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(tokenPlano.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 no disponible en este entorno", e);
        }
    }

    @Override
    public long refreshTokenExpiracionSegundos() {
        return jwtService.refreshTokenExpiracionSegundos();
    }
}
