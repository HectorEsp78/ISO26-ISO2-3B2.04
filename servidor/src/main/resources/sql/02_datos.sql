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
