-- =====================================================================
-- Esquema de la base de datos (DEC-031)
--   mysql -u radiodiagnostico -p radiodiagnostico < servidor/src/main/resources/sql/01_esquema.sql
--
-- Se puede ejecutar varias veces: no borra datos.
-- Cada caso de uso añade aquí sus tablas, en una sección propia.
-- Las pruebas del servidor ejecutan este mismo script sobre H2 (DEC-070),
-- así que solo se usa SQL compatible con H2 en modo MySQL.
-- =====================================================================

-- ---------------------------------------------------------------------
-- CU12 Gestionar pacientes (DEC-055)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS paciente (
    dni               CHAR(9)      NOT NULL,
    tarjeta_sanitaria CHAR(16)     NOT NULL,
    nombre            VARCHAR(50)  NOT NULL,
    apellidos         VARCHAR(100) NOT NULL,
    fecha_nacimiento  DATE         NULL,
    sexo              VARCHAR(6)   NULL,
    telefono          CHAR(9)      NOT NULL,
    correo            VARCHAR(100) NULL,
    direccion         VARCHAR(200) NULL,
    estado            VARCHAR(6)   NOT NULL DEFAULT 'ACTIVO',
    CONSTRAINT pk_paciente PRIMARY KEY (dni),
    CONSTRAINT uk_paciente_tarjeta UNIQUE (tarjeta_sanitaria),
    CONSTRAINT ck_paciente_sexo CHECK (sexo IN ('HOMBRE', 'MUJER')),
    CONSTRAINT ck_paciente_estado CHECK (estado IN ('ACTIVO', 'BAJA'))
);
