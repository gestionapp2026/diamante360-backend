package com.eldiamante360.auth.application.usecase;

import com.eldiamante360.auth.application.dto.CambiarPasswordCommand;

public interface CambiarPasswordUseCase {

    void ejecutar(CambiarPasswordCommand command);
}
