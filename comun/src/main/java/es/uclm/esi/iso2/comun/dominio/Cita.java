package es.uclm.esi.iso2.comun.dominio;

import java.time.LocalDateTime;

/**
 * Cita de una prueba de radiodiagnóstico (CU13, DEC-062).
 *
 * <p>Guarda el identificador del tipo de prueba y del recurso, que es lo que viaja por el
 * protocolo (DEC-065) y lo que se guarda en la base de datos (DEC-066).</p>
 *
 * @param id                identificador; {@code 0} mientras no se ha guardado
 * @param dniPaciente       DNI/NIE del paciente (CU12)
 * @param idTipoPrueba      tipo de prueba del catálogo
 * @param idRecurso         sala o equipo
 * @param inicio            fecha y hora de inicio
 * @param duracionMinutos   duración, mayor que 0
 * @param estado            estado de la cita
 * @param motivoCancelacion motivo si está cancelada; cadena vacía en otro caso
 */
public record Cita(long id, String dniPaciente, int idTipoPrueba, int idRecurso, LocalDateTime inicio,
        int duracionMinutos, EstadoCita estado, String motivoCancelacion) {

    public Cita {
        motivoCancelacion = motivoCancelacion == null ? "" : motivoCancelacion;
    }

    /** Fecha y hora de fin: inicio más duración (DEC-062). */
    public LocalDateTime fin() {
        return inicio.plusMinutes(duracionMinutos);
    }

    /**
     * Indica si las dos citas se solapan en el tiempo (RN-06, DEC-043): el inicio de cada una es
     * anterior al fin de la otra. Si una termina justo cuando empieza la otra, no se solapan.
     */
    public boolean solapaCon(Cita otra) {
        return inicio.isBefore(otra.fin()) && otra.inicio().isBefore(fin());
    }

    /** Copia de la cita con otro identificador (al guardarla por primera vez). */
    public Cita conId(long nuevoId) {
        return new Cita(nuevoId, dniPaciente, idTipoPrueba, idRecurso, inicio, duracionMinutos, estado,
                motivoCancelacion);
    }

    /** Copia de la cita con otro estado y motivo. */
    public Cita conEstado(EstadoCita nuevoEstado, String motivo) {
        return new Cita(id, dniPaciente, idTipoPrueba, idRecurso, inicio, duracionMinutos, nuevoEstado, motivo);
    }
}
