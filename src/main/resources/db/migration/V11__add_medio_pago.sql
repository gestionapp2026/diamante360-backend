-- ============================================================================
-- V11: Agrega el medio de pago (Efectivo, Nequi, Llave, Daviplata,
-- Bancolombia) a la factura y al abono. En factura es opcional: solo aplica
-- a facturas de contado, las de credito se pagan despues via abonos. En
-- abono es obligatorio: todo pago contra una cuenta por cobrar debe indicar
-- como se realizo.
-- ============================================================================

ALTER TABLE factura ADD COLUMN medio_pago VARCHAR(20) NULL;
ALTER TABLE factura ADD CONSTRAINT ck_factura_medio_pago
    CHECK (medio_pago IN ('EFECTIVO', 'NEQUI', 'LLAVE', 'DAVIPLATA', 'BANCOLOMBIA'));

ALTER TABLE abono ADD COLUMN medio_pago VARCHAR(20) NOT NULL DEFAULT 'EFECTIVO';
ALTER TABLE abono ALTER COLUMN medio_pago DROP DEFAULT;
ALTER TABLE abono ADD CONSTRAINT ck_abono_medio_pago
    CHECK (medio_pago IN ('EFECTIVO', 'NEQUI', 'LLAVE', 'DAVIPLATA', 'BANCOLOMBIA'));
