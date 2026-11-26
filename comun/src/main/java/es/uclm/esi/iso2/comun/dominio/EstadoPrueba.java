package es.uclm.esi.iso2.comun.dominio;

/** Estado de una prueba registrada en CU10 (DEC-074). */
public enum EstadoPrueba {
    /** La prueba se ha realizado: lleva fecha y hora de realización. */
    REALIZADA,
    /** La prueba no se ha realizado: lleva el motivo de la cancelación. */
    CANCELADA
}
