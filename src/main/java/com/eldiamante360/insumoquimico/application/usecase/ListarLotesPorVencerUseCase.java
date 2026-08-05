package com.eldiamante360.insumoquimico.application.usecase;

import com.eldiamante360.insumoquimico.application.dto.LoteResult;

import java.util.List;

public interface ListarLotesPorVencerUseCase {

    List<LoteResult> ejecutar(int diasUmbral);
}
