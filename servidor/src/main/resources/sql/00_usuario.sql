-- =====================================================================
-- Creación de la base de datos y del usuario de la aplicación (DEC-031)
-- Se ejecuta UNA VEZ como administrador de MySQL:
--   sudo mysql < servidor/src/main/resources/sql/00_usuario.sql
--
-- La contraseña es solo para desarrollo. En cualquier otro entorno se
-- debe cambiar y pasar al servidor con RADIODIAGNOSTICO_BD_CONTRASENA.
-- =====================================================================

CREATE DATABASE IF NOT EXISTS radiodiagnostico
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;

CREATE USER IF NOT EXISTS 'radiodiagnostico'@'localhost' IDENTIFIED BY 'radiodiagnostico';

GRANT ALL PRIVILEGES ON radiodiagnostico.* TO 'radiodiagnostico'@'localhost';

FLUSH PRIVILEGES;
