package es.uclm.esi.iso2.servidor.pruebas;

import es.uclm.esi.iso2.comun.dominio.EstadoPrueba;

/**
 * Lo mínimo que otros casos de uso (CU09) necesitan saber de una prueba (DEC-075).
 *
 * @param idPrueba    identificador de la prueba
 * @param dniPaciente paciente al que pertenece
 * @param idMedico    médico que la registró
 * @param estado      estado de la prueba
 */
public record DatosPrueba(long idPrueba, String dniPaciente, String idMedico, EstadoPrueba estado) {
}
