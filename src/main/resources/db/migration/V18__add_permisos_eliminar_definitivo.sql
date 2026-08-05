-- ============================================================================
-- V18: Permisos de eliminacion definitiva (hard delete) y ajuste de FKs de
-- tablas "hijas propias" para soportar borrado en cascada real en BD.
--
-- PRODUCTO_ELIMINAR, INSUMO_ELIMINAR, CLIENTE_ELIMINAR y USUARIO_ELIMINAR ya
-- existen desde V1 y se reusan tal cual para los nuevos endpoints DELETE de
-- esos recursos (mismo permiso que ya gatea el PATCH .../estado). Aqui solo
-- se crean los 3 permisos que todavia no existen: FACTURA_ELIMINAR,
-- ORDEN_ELIMINAR y FORMULA_ELIMINAR.
-- ============================================================================

INSERT INTO permiso (codigo, descripcion, modulo) VALUES
    ('FACTURA_ELIMINAR', 'Eliminar definitivamente una factura anulada', 'FACTURA'),
    ('ORDEN_ELIMINAR',   'Eliminar definitivamente una orden anulada',   'ORDEN'),
    ('FORMULA_ELIMINAR', 'Eliminar definitivamente una formula',        'FORMULA');

-- El borrado definitivo es una operacion de administrador: solo ADMIN recibe
-- estos 3 permisos nuevos. Deliberadamente NO se otorgan a PLANTA ni a
-- VENDEDOR (mismo criterio que V16 aplico a FORMULA_* e INSUMO_VER_NOMBRE).
INSERT INTO rol_permiso (rol_id, permiso_id)
SELECT r.id, p.id FROM rol r CROSS JOIN permiso p
WHERE r.nombre = 'ADMIN'
  AND p.codigo IN ('FACTURA_ELIMINAR', 'ORDEN_ELIMINAR', 'FORMULA_ELIMINAR');

-- ----------------------------------------------------------------------------
-- FKs de tablas "hijas propias": no tienen significado de negocio
-- independiente de su padre, asi que se pueden borrar en cascada sin
-- preguntar cuando se borra el padre. Se usa DROP CONSTRAINT + ADD
-- CONSTRAINT con el mismo nombre (el que Postgres genero por defecto para
-- cada REFERENCES inline en V2/V3/V4/V6/V7/V13) para no romper referencias
-- externas al nombre del constraint.
--
-- detalle_formula.formula_id -> formula ya tiene ON DELETE CASCADE desde
-- V15; no se toca aqui.
--
-- Deliberadamente NO se agrega cascada a: factura, orden,
-- movimiento_inventario, movimiento_insumo, cuenta_por_cobrar,
-- detalle_formula.insumo_id ni formula.producto_id — esas FKs deben seguir
-- bloqueando el DELETE de su referenciado (eso es lo que valida cada
-- Eliminar<X>Service antes de llamar al repository).
-- ----------------------------------------------------------------------------

ALTER TABLE detalle_factura
    DROP CONSTRAINT detalle_factura_factura_id_fkey,
    ADD CONSTRAINT detalle_factura_factura_id_fkey
        FOREIGN KEY (factura_id) REFERENCES factura (id) ON DELETE CASCADE;

ALTER TABLE historial_factura
    DROP CONSTRAINT historial_factura_factura_id_fkey,
    ADD CONSTRAINT historial_factura_factura_id_fkey
        FOREIGN KEY (factura_id) REFERENCES factura (id) ON DELETE CASCADE;

ALTER TABLE detalle_orden
    DROP CONSTRAINT detalle_orden_orden_id_fkey,
    ADD CONSTRAINT detalle_orden_orden_id_fkey
        FOREIGN KEY (orden_id) REFERENCES orden (id) ON DELETE CASCADE;

ALTER TABLE observacion_cliente
    DROP CONSTRAINT observacion_cliente_cliente_id_fkey,
    ADD CONSTRAINT observacion_cliente_cliente_id_fkey
        FOREIGN KEY (cliente_id) REFERENCES cliente (id) ON DELETE CASCADE;

ALTER TABLE historial_cliente
    DROP CONSTRAINT historial_cliente_cliente_id_fkey,
    ADD CONSTRAINT historial_cliente_cliente_id_fkey
        FOREIGN KEY (cliente_id) REFERENCES cliente (id) ON DELETE CASCADE;

ALTER TABLE precio_cliente_producto
    DROP CONSTRAINT precio_cliente_producto_cliente_id_fkey,
    ADD CONSTRAINT precio_cliente_producto_cliente_id_fkey
        FOREIGN KEY (cliente_id) REFERENCES cliente (id) ON DELETE CASCADE;

ALTER TABLE precio_cliente_producto
    DROP CONSTRAINT precio_cliente_producto_producto_id_fkey,
    ADD CONSTRAINT precio_cliente_producto_producto_id_fkey
        FOREIGN KEY (producto_id) REFERENCES producto (id) ON DELETE CASCADE;

ALTER TABLE lote_insumo
    DROP CONSTRAINT lote_insumo_insumo_id_fkey,
    ADD CONSTRAINT lote_insumo_insumo_id_fkey
        FOREIGN KEY (insumo_id) REFERENCES insumo_quimico (id) ON DELETE CASCADE;
