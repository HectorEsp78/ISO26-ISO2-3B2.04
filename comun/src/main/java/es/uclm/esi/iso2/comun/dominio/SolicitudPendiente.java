package es.uclm.esi.iso2.comun.dominio;

import java.time.LocalDateTime;

/**
 * Solicitud con cita programada y sin prueba registrada, lista para registrar su realización
 * (CU10, flujo 6.1). Se construye con datos de CU06, CU12 y CU13.
 *
 * @param idSolicitud    identificador de la solicitud (CU06)
 * @param dniPaciente    DNI/NIE del paciente
 * @param nombrePaciente nombre y apellidos del paciente (CU12)
 * @param tipoPrueba     tipo de prueba solicitada
 * @param prioridad      prioridad de la solicitud (CU06)
 * @param idCita         cita programada (CU13)
 * @param inicioCita     fecha y hora de inicio de la cita
 */
public record SolicitudPendiente(long idSolicitud, String dniPaciente, String nombrePaciente, String tipoPrueba,
        String prioridad, long idCita, LocalDateTime inicioCita) {
}
