package com.eldiamante360.auth.infrastructure;

import com.eldiamante360.auth.application.port.PasswordEncoderPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncoderAdapter implements PasswordEncoderPort {

    private final PasswordEncoder passwordEncoder;

    public PasswordEncoderAdapter(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String encode(String passwordPlano) {
        return passwordEncoder.encode(passwordPlano);
    }

    @Override
    public boolean matches(String passwordPlano, String passwordHash) {
        return passwordEncoder.matches(passwordPlano, passwordHash);
    }
}
