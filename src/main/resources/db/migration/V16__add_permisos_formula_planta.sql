-- ============================================================================
-- V16: Rol PLANTA (operario de planta que produce con formulas, sin ver
-- nombres de quimicos) y permisos del modulo de formulas. Tambien agrega
-- INSUMO_VER_NOMBRE, que controla si el nombre de un insumo quimico se
-- expone en las respuestas del modulo formula (mismo patron que
-- CLIENTE_VER_DOCUMENTO en V12).
-- ============================================================================

INSERT INTO rol (nombre, descripcion) VALUES
    ('PLANTA', 'Operario de planta: produce con formulas sin ver nombres de quimicos');

INSERT INTO permiso (codigo, descripcion, modulo) VALUES
    ('FORMULA_LEER',        'Consultar formulas (recetas) de produccion', 'FORMULA'),
    ('FORMULA_CREAR',       'Crear formulas (recetas) de produccion',     'FORMULA'),
    ('FORMULA_EDITAR',      'Editar formulas y cambiar su estado',        'FORMULA'),
    ('FORMULA_PRODUCIR',    'Producir un producto usando una formula',    'FORMULA'),
    ('INSUMO_VER_NOMBRE',   'Ver el nombre de los insumos quimicos (en vez de solo el numero de frasco)', 'INSUMO');

-- No se agrega FORMULA_ELIMINAR: igual que otros modulos, el estado
-- (activo/inactivo) se maneja con un toggle (PATCH /formulas/{id}/estado),
-- no con borrado.

-- ADMIN ya recibe todos los permisos via el seed inicial (V1), pero esa
-- asignacion solo corrio para los permisos que existian en ese momento.
-- Hay que asignar explicitamente los permisos nuevos aqui (igual que V12
-- hizo con CLIENTE_VER_DOCUMENTO y V14 con los permisos de ORDEN).
INSERT INTO rol_permiso (rol_id, permiso_id)
SELECT r.id, p.id FROM rol r CROSS JOIN permiso p
WHERE r.nombre = 'ADMIN'
  AND p.codigo IN ('FORMULA_LEER', 'FORMULA_CREAR', 'FORMULA_EDITAR', 'FORMULA_PRODUCIR', 'INSUMO_VER_NOMBRE');

-- PLANTA: solo lo necesario para operar el dia a dia en piso de planta
-- (ver el dashboard, consultar productos/clientes/facturas basicos, generar
-- factura de lo que se despacha, ver y producir formulas, y ver/ajustar el
-- kardex de inventario de producto). Todos estos codigos ya existen desde
-- V1 (DASHBOARD_LEER, PRODUCTO_LEER, CLIENTE_LEER, FACTURA_LEER,
-- FACTURA_CREAR, INVENTARIO_LEER) o V2 (INVENTARIO_AJUSTAR).
INSERT INTO rol_permiso (rol_id, permiso_id)
SELECT r.id, p.id FROM rol r CROSS JOIN permiso p
WHERE r.nombre = 'PLANTA'
  AND p.codigo IN (
      'DASHBOARD_LEER',
      'PRODUCTO_LEER',
      'CLIENTE_LEER',
      'FACTURA_LEER',
      'FACTURA_CREAR',
      'FORMULA_LEER',
      'FORMULA_PRODUCIR',
      'INVENTARIO_LEER',
      'INVENTARIO_AJUSTAR'
  );

-- PLANTA deliberadamente NO recibe CLIENTE_VER_DOCUMENTO ni
-- INSUMO_VER_NOMBRE: el operario de planta nunca debe ver la cedula/RUC de
-- clientes ni el nombre de los quimicos, solo el numero de frasco fisico
-- que debe usar (regla de negocio central de este modulo).

-- Los nuevos permisos FORMULA_* e INSUMO_VER_NOMBRE deliberadamente NO se
-- otorgan a VENDEDOR: ese rol no participa en la produccion de planta.
