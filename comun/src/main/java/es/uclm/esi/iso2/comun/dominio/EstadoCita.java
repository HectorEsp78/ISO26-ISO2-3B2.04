package es.uclm.esi.iso2.comun.dominio;

/**
 * Estado de una cita (CU13, DEC-042 y DEC-062).
 *
 * <p>Toda cita nace {@code PROGRAMADA}. Solo las citas {@code PROGRAMADA} ocupan hueco en la
 * agenda: las {@code CANCELADA} y {@code REALIZADA} no cuentan para los solapes.
 * {@code REALIZADA} solo la pone CU10 (DEC-063).</p>
 */
public enum EstadoCita {
    PROGRAMADA,
    CANCELADA,
    REALIZADA
}
