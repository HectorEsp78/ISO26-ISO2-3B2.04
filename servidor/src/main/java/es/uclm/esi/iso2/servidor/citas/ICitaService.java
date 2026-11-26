package es.uclm.esi.iso2.servidor.citas;

import es.uclm.esi.iso2.comun.dominio.Cita;
import es.uclm.esi.iso2.comun.dominio.DatosCita;
import es.uclm.esi.iso2.comun.dominio.EstadoCita;
import es.uclm.esi.iso2.comun.dominio.Recurso;
import es.uclm.esi.iso2.comun.dominio.TipoPrueba;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Operaciones sobre citas (DEC-063). El resto del servidor (CU10, CU12 y, más adelante, CU01,
 * CU06, CU14 y CU15) solo depende de esta interfaz.
 *
 * <p>Si una operación se rechaza, lanza {@link CitaException} con el código de la DEC-065 y no
 * cambia nada.</p>
 */
public interface ICitaService {

    /** CU13: crea una cita {@code PROGRAMADA}. Comprueba RN-01 a RN-06. */
    Cita asignarCita(DatosCita datos);

    /**
     * CU13: citas que cumplen los filtros, ordenadas por inicio. Cualquier filtro puede ser
     * {@code null} («sin filtro»). Las fechas incluyen el día completo.
     */
    List<Cita> buscarCitas(String dni, LocalDate desde, LocalDate hasta, EstadoCita estado);

    /**
     * CU13: cambia recurso, inicio y/o duración de la misma cita (DEC-045). Un valor {@code null}
     * deja el que tenía. Los solapes se comprueban sin contar la propia cita (RN-07).
     */
    Cita reprogramarCita(long id, Integer idRecurso, LocalDateTime inicio, Integer duracionMinutos);

    /** CU13: pasa la cita a {@code CANCELADA} con su motivo (DEC-046). Aplica RN-08. */
    Cita cancelarCita(long id, String motivo);

    /** CU13: catálogo de tipos de prueba (DEC-047). */
    List<TipoPrueba> listarTiposPrueba();

    /** CU13: lista de recursos (DEC-047). */
    List<Recurso> listarRecursos();

    /** CU10: la cita con ese id. */
    Cita obtenerCita(long id);

    /** CU10: pasa una cita {@code PROGRAMADA} a {@code REALIZADA}. No aplica RN-08. */
    Cita marcarRealizada(long id);

    /** CU10: pasa una cita {@code PROGRAMADA} a {@code CANCELADA} con su motivo. No aplica RN-08. */
    Cita marcarCancelada(long id, String motivo);

    /**
     * CU12: al dar de baja a un paciente, cancela todas sus citas {@code PROGRAMADA} con el motivo
     * indicado (DEC-048).
     *
     * @return cuántas citas se han cancelado (puede ser 0)
     */
    int cancelarCitasPendientes(String dni, String motivo);
}
