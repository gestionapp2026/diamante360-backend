-- ============================================================================
-- V9: Agrega el precio de compra (global, a nivel de catalogo) al insumo
-- quimico. Nullable: los insumos existentes no tienen este dato capturado.
-- ============================================================================

ALTER TABLE insumo_quimico
    ADD COLUMN precio_compra NUMERIC(12,2) NULL;
