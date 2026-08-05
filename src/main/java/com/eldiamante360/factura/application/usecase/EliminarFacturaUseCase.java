package com.eldiamante360.factura.application.usecase;

public interface EliminarFacturaUseCase {

    void ejecutar(Long id, boolean cascada);
}
