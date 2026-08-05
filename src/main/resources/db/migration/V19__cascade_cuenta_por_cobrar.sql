-- ============================================================================
-- V19: cascada faltante en las tablas hijas de cuenta_por_cobrar.
--
-- V18 dejo cuenta_por_cobrar SIN cascade hacia factura/cliente (correcto,
-- es una dependencia que debe seguir bloqueando el DELETE de esos dos). Pero
-- no se considero que cuenta_por_cobrar tiene sus PROPIAS tablas hijas
-- (abono, historial_cuenta_por_cobrar) que SI son propiedad exclusiva de la
-- cuenta por cobrar (igual que detalle_factura lo es de factura). Sin esta
-- cascada, el borrado en cascada de un cliente/factura con cuenta_por_cobrar
-- que ya tiene abonos o historial falla con una violacion de FK a mitad de
-- transaccion (detectado probando en vivo el DELETE .../cascada=true).
-- ============================================================================

ALTER TABLE abono
    DROP CONSTRAINT abono_cuenta_por_cobrar_id_fkey,
    ADD CONSTRAINT abono_cuenta_por_cobrar_id_fkey
        FOREIGN KEY (cuenta_por_cobrar_id) REFERENCES cuenta_por_cobrar (id) ON DELETE CASCADE;

ALTER TABLE historial_cuenta_por_cobrar
    DROP CONSTRAINT historial_cuenta_por_cobrar_cuenta_por_cobrar_id_fkey,
    ADD CONSTRAINT historial_cuenta_por_cobrar_cuenta_por_cobrar_id_fkey
        FOREIGN KEY (cuenta_por_cobrar_id) REFERENCES cuenta_por_cobrar (id) ON DELETE CASCADE;
