-- Permite que un cliente tenga mas de un telefono. Se reemplaza la columna
-- escalar `telefono` por un arreglo `telefonos`, migrando el valor existente
-- (si lo habia) como primer elemento.
ALTER TABLE cliente ADD COLUMN telefonos TEXT[] NOT NULL DEFAULT '{}';

UPDATE cliente
SET telefonos = ARRAY[telefono]
WHERE telefono IS NOT NULL AND telefono <> '';

ALTER TABLE cliente ALTER COLUMN telefonos DROP DEFAULT;

ALTER TABLE cliente DROP COLUMN telefono;
