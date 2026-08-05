-- ============================================================================
-- V14: Permisos del modulo de ordenes/pedidos anticipados. Cualquier rol
-- existente (ADMIN y VENDEDOR) puede crear ordenes: un vendedor en campo
-- levanta el pedido y, en la practica, tambien es quien opera el ciclo
-- completo (consultar/despachar/anular) dia a dia.
-- ============================================================================

INSERT INTO permiso (codigo, descripcion, modulo) VALUES
    ('ORDEN_LEER',      'Consultar ordenes/pedidos',        'ORDEN'),
    ('ORDEN_CREAR',     'Crear ordenes/pedidos',            'ORDEN'),
    ('ORDEN_DESPACHAR', 'Marcar ordenes como despachadas',  'ORDEN'),
    ('ORDEN_ANULAR',    'Anular ordenes/pedidos',           'ORDEN');

-- ADMIN ya recibe todo via el seed original, pero hay que asignar
-- explicitamente los permisos nuevos (igual que V12 hizo con CLIENTE_VER_DOCUMENTO).
INSERT INTO rol_permiso (rol_id, permiso_id)
SELECT r.id, p.id FROM rol r CROSS JOIN permiso p
WHERE r.nombre = 'ADMIN' AND p.codigo IN ('ORDEN_LEER','ORDEN_CREAR','ORDEN_DESPACHAR','ORDEN_ANULAR');

-- VENDEDOR: "cualquier rol" debe poder crear ordenes segun el requerimiento
-- del cliente; se le da el ciclo completo (leer/crear/despachar/anular)
-- ya que en la practica el vendedor es quien opera este modulo dia a dia.
INSERT INTO rol_permiso (rol_id, permiso_id)
SELECT r.id, p.id FROM rol r CROSS JOIN permiso p
WHERE r.nombre = 'VENDEDOR' AND p.codigo IN ('ORDEN_LEER','ORDEN_CREAR','ORDEN_DESPACHAR','ORDEN_ANULAR');
