package es.uclm.esi.iso2.servidor.pruebas;

/**
 * Datos de una solicitud de prueba (CU06) que CU10 necesita.
 *
 * @param idSolicitud  identificador de la solicitud
 * @param dniPaciente  paciente
 * @param idMedico     médico solicitante: es de quien es el paciente en esta solicitud (RN-02)
 * @param tipoPrueba   nombre del tipo de prueba
 * @param prioridad    prioridad
 * @param idCita       cita asignada (CU13), o {@code null} si no tiene
 */
public record SolicitudDePrueba(long idSolicitud, String dniPaciente, String idMedico, String tipoPrueba,
        String prioridad, Long idCita) {
}
