-- =====================================================================
-- Datos de ejemplo para desarrollo y pruebas manuales (DEC-031)
--   mysql -u radiodiagnostico -p radiodiagnostico < servidor/src/main/resources/sql/02_datos.sql
--
-- Se puede ejecutar varias veces: los registros que ya existen se ignoran.
-- Son los mismos pacientes que el modo sin servidor del cliente.
-- =====================================================================

-- ---------------------------------------------------------------------
-- CU12 Gestionar pacientes
-- ---------------------------------------------------------------------
INSERT IGNORE INTO paciente
    (dni, tarjeta_sanitaria, nombre, apellidos, fecha_nacimiento, sexo, telefono, correo, direccion, estado)
VALUES
    ('12345678Z', 'CMCM000000000001', 'Lucía', 'García Martín', '1985-04-12', 'MUJER', '600111222',
     'lucia.garcia@example.com', 'C/ Mayor 1, Ciudad Real', 'ACTIVO'),
    ('X1234567L', 'CMCM000000000002', 'Ahmed', 'El Idrissi', '1990-11-30', 'HOMBRE', '699333444',
     NULL, NULL, 'ACTIVO'),
    ('87654321X', 'CMCM000000000003', 'Antonio', 'López Ruiz', '1948-02-01', 'HOMBRE', '926555666',
     NULL, 'Avda. de la Mancha 5, Toledo', 'BAJA');

-- ---------------------------------------------------------------------
-- CU13 Gestionar citas médicas: catálogo de ejemplo (DEC-047).
-- Valores inventados para desarrollo, no son un catálogo clínico real.
-- ---------------------------------------------------------------------
INSERT IGNORE INTO tipo_prueba (id, nombre, duracion_minutos)
VALUES
    (1, 'Radiografía de tórax', 15),
    (2, 'Ecografía abdominal', 30),
    (3, 'Mamografía', 20),
    (4, 'TAC craneal', 30),
    (5, 'Resonancia magnética de rodilla', 45);

INSERT IGNORE INTO recurso (id, nombre)
VALUES
    (1, 'Sala de rayos X 1'),
    (2, 'Ecógrafo 1'),
    (3, 'Mamógrafo 1'),
    (4, 'TAC 1'),
    (5, 'Resonancia 1');
