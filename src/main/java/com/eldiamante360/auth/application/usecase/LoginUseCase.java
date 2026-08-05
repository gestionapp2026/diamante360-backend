package com.eldiamante360.auth.application.usecase;

import com.eldiamante360.auth.application.dto.LoginCommand;
import com.eldiamante360.auth.application.dto.SesionResult;

public interface LoginUseCase {

    SesionResult ejecutar(LoginCommand command);
}
