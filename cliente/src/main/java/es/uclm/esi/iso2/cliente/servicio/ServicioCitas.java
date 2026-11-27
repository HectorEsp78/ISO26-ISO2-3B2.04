package es.uclm.esi.iso2.cliente.servicio;

import es.uclm.esi.iso2.comun.dominio.Cita;
import es.uclm.esi.iso2.comun.dominio.DatosCita;
import es.uclm.esi.iso2.comun.dominio.EstadoCita;
import es.uclm.esi.iso2.comun.dominio.Recurso;
import es.uclm.esi.iso2.comun.dominio.TipoPrueba;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Operaciones de CU13 que el cliente pide al servidor (DEC-065). Es el equivalente en el cliente
 * de {@code ICitaService}, con las operaciones que tienen mensaje en el protocolo.
 *
 * <p>Todas lanzan {@link ExcepcionServicioCitas} si el servidor rechaza la operación o no se puede
 * contactar con él.</p>
 */
public interface ServicioCitas {

    /** Asigna una cita nueva (flujo básico). Nace en estado PROGRAMADA. */
    Cita asignar(DatosCita datos);

    /** Busca citas. Cualquier filtro puede ser {@code null}. Ordenadas por fecha de inicio. */
    List<Cita> buscar(String dni, LocalDate desde, LocalDate hasta, EstadoCita estado);

    /**
     * Cambia el recurso, el inicio y/o la duración de la misma cita (DEC-045). Un valor
     * {@code null} deja ese dato como estaba.
     */
    Cita reprogramar(long id, Integer idRecurso, LocalDateTime inicio, Integer duracionMinutos);

    /** Pasa la cita a CANCELADA con un motivo (DEC-046). No la borra. */
    Cita cancelar(long id, String motivo);

    /** Tipos de prueba del catálogo de ejemplo (DEC-047). */
    List<TipoPrueba> listarTiposPrueba();

    /** Salas y equipos de ejemplo (DEC-047). */
    List<Recurso> listarRecursos();
}
