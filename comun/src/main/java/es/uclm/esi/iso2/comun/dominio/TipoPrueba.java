package es.uclm.esi.iso2.comun.dominio;

/**
 * Tipo de prueba del catálogo de ejemplo (DEC-047 y DEC-062).
 *
 * @param id              identificador en la base de datos
 * @param nombre          nombre que se muestra al usuario
 * @param duracionMinutos duración propuesta para las citas de este tipo
 */
public record TipoPrueba(int id, String nombre, int duracionMinutos) {
}
