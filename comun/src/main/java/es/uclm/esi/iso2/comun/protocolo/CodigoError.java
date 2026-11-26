package es.uclm.esi.iso2.comun.protocolo;

/**
 * Códigos de error del protocolo (DEC-057).
 */
public enum CodigoError {
    /** Falta un campo obligatorio. La respuesta indica el campo. */
    CAMPO_OBLIGATORIO,
    /** Un campo no tiene un formato válido. La respuesta indica el campo. */
    CAMPO_NO_VALIDO,
    /** Ya existe un paciente con ese DNI/NIE (RN-01). */
    DNI_DUPLICADO,
    /** Ya existe un paciente con ese número de tarjeta sanitaria. */
    TARJETA_DUPLICADA,
    /** No existe ningún paciente con ese DNI/NIE. */
    PACIENTE_NO_ENCONTRADO,
    /** El paciente está dado de baja: no se puede modificar ni dar de baja otra vez. */
    PACIENTE_DE_BAJA,
    /** Operación desconocida o número de campos incorrecto. */
    PETICION_NO_VALIDA,
    /** Fallo interno del servidor, por ejemplo de la base de datos. */
    ERROR_INTERNO,
    /** Solo lo genera el cliente: no se ha podido conectar con el servidor (FA-08). */
    SIN_CONEXION,
    /** Solo lo genera el cliente: el servidor ha respondido algo que no sigue el protocolo. */
    RESPUESTA_NO_VALIDA
}
