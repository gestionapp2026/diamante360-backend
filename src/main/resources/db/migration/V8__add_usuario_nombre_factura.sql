-- ============================================================================
-- V8: Agrega el nombre del usuario que emitio la factura como snapshot
-- congelado (mismo patron que cliente_nombre/cliente_numero_documento), para
-- que las facturas antiguas conserven el nombre aunque el usuario cambie de
-- nombre o sea eliminado mas adelante.
-- ============================================================================

ALTER TABLE factura ADD COLUMN usuario_nombre VARCHAR(150);

UPDATE factura f
SET usuario_nombre = u.nombre_completo
FROM usuario u
WHERE u.id = f.usuario_id;

ALTER TABLE factura ALTER COLUMN usuario_nombre SET NOT NULL;
