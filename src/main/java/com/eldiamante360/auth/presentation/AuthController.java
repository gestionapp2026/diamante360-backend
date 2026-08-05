package com.eldiamante360.auth.presentation;

import com.eldiamante360.auth.application.dto.LogoutCommand;
import com.eldiamante360.auth.application.usecase.LoginUseCase;
import com.eldiamante360.auth.application.usecase.LogoutUseCase;
import com.eldiamante360.auth.application.usecase.ObtenerSesionActualUseCase;
import com.eldiamante360.auth.application.usecase.RefreshTokenUseCase;
import com.eldiamante360.auth.presentation.dto.request.LoginRequest;
import com.eldiamante360.auth.presentation.dto.request.RefreshTokenRequest;
import com.eldiamante360.auth.presentation.dto.response.TokenResponse;
import com.eldiamante360.auth.presentation.dto.response.UsuarioSesionResponse;
import com.eldiamante360.auth.presentation.mapper.AuthWebMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final LogoutUseCase logoutUseCase;
    private final ObtenerSesionActualUseCase obtenerSesionActualUseCase;
    private final AuthWebMapper authWebMapper;

    public AuthController(LoginUseCase loginUseCase,
                           RefreshTokenUseCase refreshTokenUseCase,
                           LogoutUseCase logoutUseCase,
                           ObtenerSesionActualUseCase obtenerSesionActualUseCase,
                           AuthWebMapper authWebMapper) {
        this.loginUseCase = loginUseCase;
        this.refreshTokenUseCase = refreshTokenUseCase;
        this.logoutUseCase = logoutUseCase;
        this.obtenerSesionActualUseCase = obtenerSesionActualUseCase;
        this.authWebMapper = authWebMapper;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        var command = authWebMapper.toCommand(request, obtenerIp(httpRequest), httpRequest.getHeader("User-Agent"));
        var resultado = loginUseCase.ejecutar(command);
        return ResponseEntity.ok(authWebMapper.toResponse(resultado));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest request, HttpServletRequest httpRequest) {
        var command = authWebMapper.toCommand(request, obtenerIp(httpRequest), httpRequest.getHeader("User-Agent"));
        var resultado = refreshTokenUseCase.ejecutar(command);
        return ResponseEntity.ok(authWebMapper.toResponse(resultado));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequest request) {
        logoutUseCase.ejecutar(new LogoutCommand(request.refreshToken()));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioSesionResponse> me(Authentication authentication) {
        var resumen = obtenerSesionActualUseCase.ejecutar(authentication.getName());
        return ResponseEntity.ok(new UsuarioSesionResponse(
                resumen.id(), resumen.username(), resumen.nombreCompleto(),
                resumen.rol(), resumen.permisos(), resumen.debeCambiarPassword()));
    }

    private String obtenerIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
