package es.uclm.esi.iso2.servidor.pruebas;

import es.uclm.esi.iso2.comun.dominio.Cita;
import es.uclm.esi.iso2.comun.dominio.DatosCita;
import es.uclm.esi.iso2.comun.dominio.EstadoCita;
import es.uclm.esi.iso2.comun.dominio.Recurso;
import es.uclm.esi.iso2.comun.dominio.TipoPrueba;
import es.uclm.esi.iso2.comun.protocolo.CodigoError;
import es.uclm.esi.iso2.servidor.citas.CitaException;
import es.uclm.esi.iso2.servidor.citas.ICitaService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/** CU13 que lee bien pero rechaza los cambios de estado, como si la cita hubiera cambiado a la vez. */
final class CitasQueFallan implements ICitaService {

    private final ICitaService real;

    CitasQueFallan(ICitaService real) {
        this.real = real;
    }

    @Override
    public Cita obtenerCita(long id) {
        return real.obtenerCita(id);
    }

    @Override
    public Cita marcarRealizada(long id) {
        throw new CitaException(CodigoError.CITA_NO_MODIFICABLE);
    }

    @Override
    public Cita marcarCancelada(long id, String motivo) {
        throw new CitaException(CodigoError.CITA_NO_MODIFICABLE);
    }

    @Override
    public Cita asignarCita(DatosCita datos) {
        return real.asignarCita(datos);
    }

    @Override
    public List<Cita> buscarCitas(String dni, LocalDate desde, LocalDate hasta, EstadoCita estado) {
        return real.buscarCitas(dni, desde, hasta, estado);
    }

    @Override
    public Cita reprogramarCita(long id, Integer idRecurso, LocalDateTime inicio, Integer duracionMinutos) {
        return real.reprogramarCita(id, idRecurso, inicio, duracionMinutos);
    }

    @Override
    public Cita cancelarCita(long id, String motivo) {
        return real.cancelarCita(id, motivo);
    }

    @Override
    public List<TipoPrueba> listarTiposPrueba() {
        return real.listarTiposPrueba();
    }

    @Override
    public List<Recurso> listarRecursos() {
        return real.listarRecursos();
    }

    @Override
    public int cancelarCitasPendientes(String dni, String motivo) {
        return real.cancelarCitasPendientes(dni, motivo);
    }
}
