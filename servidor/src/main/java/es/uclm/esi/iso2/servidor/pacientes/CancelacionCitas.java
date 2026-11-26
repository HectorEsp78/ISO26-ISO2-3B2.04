package es.uclm.esi.iso2.servidor.pacientes;

/**
 * Cancela las citas pendientes de un paciente al darlo de baja (DEC-048).
 *
 * <p>La implementa CU13: cuando exista {@code CitaService}, se pasa
 * {@code citaService::cancelarCitasPendientes} al crear {@link PacienteService} (DEC-063 y
 * DEC-068). Mientras tanto se usa {@link #NINGUNA}.</p>
 */
@FunctionalInterface
public interface CancelacionCitas {

    /** Motivo con el que se cancelan las citas (DEC-048). */
    String MOTIVO_BAJA = "Baja del paciente";

    /** No cancela nada: para cuando todavía no existen las citas. */
    CancelacionCitas NINGUNA = (dni, motivo) -> 0;

    /**
     * Pasa a {@code CANCELADA} todas las citas {@code PROGRAMADA} del paciente.
     *
     * @return cuántas citas se han cancelado
     */
    int cancelarCitasPendientes(String dni, String motivo);
}
