package com.eldiamante360.insumoquimico.application.usecase;

import com.eldiamante360.insumoquimico.application.dto.LoteResult;
import com.eldiamante360.insumoquimico.application.port.LoteInsumoRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ListarLotesPorVencerService implements ListarLotesPorVencerUseCase {

    private final LoteInsumoRepositoryPort loteInsumoRepositoryPort;

    public ListarLotesPorVencerService(LoteInsumoRepositoryPort loteInsumoRepositoryPort) {
        this.loteInsumoRepositoryPort = loteInsumoRepositoryPort;
    }

    @Override
    public List<LoteResult> ejecutar(int diasUmbral) {
        LocalDate hasta = LocalDate.now().plusDays(diasUmbral);
        return loteInsumoRepositoryPort.listarPorVencer(hasta).stream()
                .map(InsumoQuimicoAssembler::toResult)
                .toList();
    }
}
