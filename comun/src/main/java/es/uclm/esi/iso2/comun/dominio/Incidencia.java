package es.uclm.esi.iso2.comun.dominio;

import java.time.LocalDateTime;

/**
 * Incidencia ocurrida durante una prueba (DEC-074).
 *
 * @param id          identificador; {@code 0} mientras no se ha guardado
 * @param idPrueba    prueba a la que pertenece; {@code 0} mientras no se ha guardado
 * @param tipo        tipo de incidencia
 * @param descripcion descripción, de 1 a 500 caracteres
 * @param fechaHora   fecha y hora de alta; la asigna el servidor
 */
public record Incidencia(long id, long idPrueba, TipoIncidencia tipo, String descripcion,
        LocalDateTime fechaHora) {

    /** Copia de la incidencia con otros identificadores (al guardarla). */
    public Incidencia conIds(long nuevoId, long nuevaPrueba) {
        return new Incidencia(nuevoId, nuevaPrueba, tipo, descripcion, fechaHora);
    }
}
