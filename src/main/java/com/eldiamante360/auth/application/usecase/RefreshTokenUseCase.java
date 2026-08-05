package com.eldiamante360.auth.application.usecase;

import com.eldiamante360.auth.application.dto.RefreshTokenCommand;
import com.eldiamante360.auth.application.dto.SesionResult;

public interface RefreshTokenUseCase {

    SesionResult ejecutar(RefreshTokenCommand command);
}
