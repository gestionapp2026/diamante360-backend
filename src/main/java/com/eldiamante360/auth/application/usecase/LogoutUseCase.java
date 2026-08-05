package com.eldiamante360.auth.application.usecase;

import com.eldiamante360.auth.application.dto.LogoutCommand;

public interface LogoutUseCase {

    void ejecutar(LogoutCommand command);
}
