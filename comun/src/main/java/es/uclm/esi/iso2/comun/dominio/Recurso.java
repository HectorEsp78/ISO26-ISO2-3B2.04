package es.uclm.esi.iso2.comun.dominio;

/**
 * Sala o equipo en el que se hace una prueba (DEC-047 y DEC-062).
 *
 * @param id     identificador en la base de datos
 * @param nombre nombre que se muestra al usuario
 */
public record Recurso(int id, String nombre) {
}
