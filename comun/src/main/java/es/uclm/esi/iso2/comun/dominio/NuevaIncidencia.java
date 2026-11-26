package es.uclm.esi.iso2.comun.dominio;

/**
 * Datos que introduce el médico al registrar una incidencia (DEC-074).
 *
 * @param tipo        tipo de incidencia (obligatorio)
 * @param descripcion descripción (obligatoria, de 1 a 500 caracteres)
 */
public record NuevaIncidencia(TipoIncidencia tipo, String descripcion) {
}
