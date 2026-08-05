package com.eldiamante360.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Extrae el access token del header Authorization, lo valida y, si es
 * correcto, puebla el SecurityContext con un Authentication cuyas
 * authorities son ROLE_{rol} + cada codigo de permiso embebido en el
 * token. No consulta base de datos: toda la informacion viene del JWT.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                     @NonNull HttpServletResponse response,
                                     @NonNull FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith(BEARER_PREFIX)) {
            String token = header.substring(BEARER_PREFIX.length());
            try {
                autenticar(token, request);
            } catch (JwtException | IllegalArgumentException ex) {
                log.debug("Access token invalido: {}", ex.getMessage());
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }

    private void autenticar(String token, HttpServletRequest request) {
        Claims claims = jwtService.validarYObtenerClaims(token);
        String username = jwtService.obtenerUsername(claims);

        List<GrantedAuthority> authorities = new ArrayList<>();
        String rol = jwtService.obtenerRol(claims);
        if (rol != null) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + rol));
        }
        List<String> permisos = jwtService.obtenerPermisos(claims);
        if (permisos != null) {
            permisos.forEach(codigo -> authorities.add(new SimpleGrantedAuthority(codigo)));
        }

        var authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
