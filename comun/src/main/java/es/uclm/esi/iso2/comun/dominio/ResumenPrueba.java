package es.uclm.esi.iso2.comun.dominio;

import java.time.LocalDateTime;

/**
 * Prueba sin sus incidencias, con solo cuántas tiene. Es lo que devuelve la búsqueda de pruebas
 * (DEC-078).
 */
public record ResumenPrueba(long id, long idSolicitud, String dniPaciente, String idMedico, EstadoPrueba estado,
        LocalDateTime fechaHoraRealizacion, String observaciones, String motivoCancelacion,
        LocalDateTime fechaRegistro, int numIncidencias) {
}
