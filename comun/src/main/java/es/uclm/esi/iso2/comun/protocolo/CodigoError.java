package es.uclm.esi.iso2.comun.protocolo;

/**
 * Códigos de error del protocolo (DEC-057).
 *
 * <p>Reglas para añadir códigos (acordadas por el equipo): cada caso de uso tiene su bloque, con un
 * comentario {@code // --- CUxx ---}, y añade sus códigos solo en él, una constante por línea. Cada
 * constante termina en coma, también la última, para que añadir un código no modifique líneas de
 * otros. Si hay un conflicto al integrar, se conservan los códigos de las dos partes.</p>
 */
public enum CodigoError {

    // --- Generales (de cualquier caso de uso) ---
    /** Falta un campo obligatorio. La respuesta indica el campo. */
    CAMPO_OBLIGATORIO,
    /** Un campo no tiene un formato válido. La respuesta indica el campo. */
    CAMPO_NO_VALIDO,
    /** Operación desconocida o número de campos incorrecto. */
    PETICION_NO_VALIDA,
    /** Fallo interno del servidor, por ejemplo de la base de datos. */
    ERROR_INTERNO,
    /** Solo lo genera el cliente: no se ha podido conectar con el servidor (FA-08). */
    SIN_CONEXION,
    /** Solo lo genera el cliente: el servidor ha respondido algo que no sigue el protocolo. */
    RESPUESTA_NO_VALIDA,

    // --- CU12 ---
    /** Ya existe un paciente con ese DNI/NIE (RN-01). */
    DNI_DUPLICADO,
    /** Ya existe un paciente con ese número de tarjeta sanitaria. */
    TARJETA_DUPLICADA,
    /** No existe ningún paciente con ese DNI/NIE. */
    PACIENTE_NO_ENCONTRADO,
    /** El paciente está dado de baja: no se puede modificar ni dar de baja otra vez. */
    PACIENTE_DE_BAJA,

    // --- CU13 ---
    /** El inicio de la cita es anterior al momento actual (RN-02). */
    FECHA_PASADA,
    /** La cita no empieza y termina de lunes a viernes entre las 08:00 y las 20:00 (RN-03, DEC-044). */
    FUERA_DE_HORARIO,
    /** Choca con otra cita PROGRAMADA del mismo recurso (RN-04). La respuesta indica su id. */
    SOLAPE_RECURSO,
    /** Choca con otra cita PROGRAMADA del mismo paciente (RN-05). La respuesta indica su id. */
    SOLAPE_PACIENTE,
    /** No existe ninguna cita con ese identificador. */
    CITA_NO_ENCONTRADA,
    /** La cita no está PROGRAMADA o, al reprogramar o cancelar, ya ha empezado (RN-08). */
    CITA_NO_MODIFICABLE,
}
