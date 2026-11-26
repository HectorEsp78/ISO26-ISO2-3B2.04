package es.uclm.esi.iso2.servidor.citas;

import es.uclm.esi.iso2.comun.dominio.Cita;
import es.uclm.esi.iso2.comun.dominio.DatosCita;
import es.uclm.esi.iso2.comun.dominio.EstadoCita;
import es.uclm.esi.iso2.comun.dominio.EstadoPaciente;
import es.uclm.esi.iso2.comun.dominio.Paciente;
import es.uclm.esi.iso2.comun.dominio.Recurso;
import es.uclm.esi.iso2.comun.dominio.TipoPrueba;
import es.uclm.esi.iso2.comun.dominio.ValidadorPaciente;
import es.uclm.esi.iso2.comun.protocolo.CodigoError;
import es.uclm.esi.iso2.comun.protocolo.ProtocoloCitas;
import es.uclm.esi.iso2.servidor.pacientes.PacienteException;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Reglas de negocio de las citas (CU13, DEC-063).
 *
 * <p>Orden de las comprobaciones al asignar o reprogramar: datos obligatorios y formato,
 * catálogo, paciente activo (RN-01), fecha futura (RN-02), horario (RN-03) y, por último,
 * solapes (RN-04 a RN-07), que se comprueban al guardar dentro de una transacción.</p>
 */
public class CitaService implements ICitaService {

    /** Horario provisional de la DEC-044: de lunes a viernes, de 08:00 a 20:00. */
    public static final LocalTime APERTURA = LocalTime.of(8, 0);
    public static final LocalTime CIERRE = LocalTime.of(20, 0);
    /** Longitud máxima del motivo de cancelación (DEC-046). */
    public static final int MAX_MOTIVO = 255;

    private final CitaDAO dao;
    private final ComprobacionPaciente pacientes;
    private final Clock reloj;

    public CitaService(CitaDAO dao, ComprobacionPaciente pacientes) {
        this(dao, pacientes, Clock.systemDefaultZone());
    }

    /** Permite fijar el momento actual en las pruebas. */
    public CitaService(CitaDAO dao, ComprobacionPaciente pacientes, Clock reloj) {
        this.dao = dao;
        this.pacientes = pacientes;
        this.reloj = reloj;
    }

    // ---- CU13 ----

    @Override
    public Cita asignarCita(DatosCita datos) {
        String dni = dniObligatorio(datos.dniPaciente());
        Integer idTipo = obligatorio(datos.idTipoPrueba(), ProtocoloCitas.CAMPO_TIPO_PRUEBA);
        Integer idRecurso = obligatorio(datos.idRecurso(), ProtocoloCitas.CAMPO_RECURSO);
        LocalDateTime inicio = obligatorio(datos.inicio(), ProtocoloCitas.CAMPO_INICIO);

        TipoPrueba tipo = dao.buscarTipoPrueba(idTipo)
                .orElseThrow(() -> noValido(ProtocoloCitas.CAMPO_TIPO_PRUEBA));
        comprobarRecurso(idRecurso);
        int duracion = datos.duracionMinutos() == null ? tipo.duracionMinutos() : datos.duracionMinutos();

        Cita nueva = new Cita(0, dni, idTipo, idRecurso, inicio, duracion, EstadoCita.PROGRAMADA, "");
        comprobarCita(nueva);
        return dao.guardarSinSolapes(nueva);
    }

    @Override
    public List<Cita> buscarCitas(String dni, LocalDate desde, LocalDate hasta, EstadoCita estado) {
        if (desde != null && hasta != null && hasta.isBefore(desde)) {
            throw noValido(ProtocoloCitas.CAMPO_HASTA);
        }
        String normalizado = Paciente.normalizarDni(dni);
        return dao.buscar(normalizado.isEmpty() ? null : normalizado, desde, hasta, estado);
    }

    @Override
    public Cita reprogramarCita(long id, Integer idRecurso, LocalDateTime inicio, Integer duracionMinutos) {
        Cita actual = modificable(id);
        int nuevoRecurso = idRecurso == null ? actual.idRecurso() : idRecurso;
        if (idRecurso != null) {
            comprobarRecurso(idRecurso);
        }
        Cita nueva = new Cita(actual.id(), actual.dniPaciente(), actual.idTipoPrueba(), nuevoRecurso,
                inicio == null ? actual.inicio() : inicio,
                duracionMinutos == null ? actual.duracionMinutos() : duracionMinutos,
                EstadoCita.PROGRAMADA, "");
        comprobarCita(nueva);
        return dao.guardarSinSolapes(nueva);
    }

    @Override
    public Cita cancelarCita(long id, String motivo) {
        String m = motivoValido(motivo);
        modificable(id);
        return cambiarEstado(id, EstadoCita.CANCELADA, m);
    }

    @Override
    public List<TipoPrueba> listarTiposPrueba() {
        return dao.listarTiposPrueba();
    }

    @Override
    public List<Recurso> listarRecursos() {
        return dao.listarRecursos();
    }

    // ---- CU10 ----

    @Override
    public Cita obtenerCita(long id) {
        return dao.buscarPorId(id).orElseThrow(() -> new CitaException(CodigoError.CITA_NO_ENCONTRADA));
    }

    @Override
    public Cita marcarRealizada(long id) {
        obtenerCita(id);
        return cambiarEstado(id, EstadoCita.REALIZADA, null);
    }

    @Override
    public Cita marcarCancelada(long id, String motivo) {
        String m = motivoValido(motivo);
        obtenerCita(id);
        return cambiarEstado(id, EstadoCita.CANCELADA, m);
    }

    // ---- CU12 ----

    @Override
    public int cancelarCitasPendientes(String dni, String motivo) {
        String m = motivoValido(motivo);
        return dao.cancelarPendientes(dniObligatorio(dni), m);
    }

    // ---- Reglas ----

    /** Duración (DEC-062), paciente activo (RN-01), fecha futura (RN-02) y horario (RN-03). */
    private void comprobarCita(Cita cita) {
        if (cita.duracionMinutos() <= 0) {
            throw noValido(ProtocoloCitas.CAMPO_DURACION);
        }
        comprobarPaciente(cita.dniPaciente());
        if (!cita.inicio().isAfter(LocalDateTime.now(reloj))) {
            throw new CitaException(CodigoError.FECHA_PASADA, ProtocoloCitas.CAMPO_INICIO);
        }
        if (!dentroDeHorario(cita)) {
            throw new CitaException(CodigoError.FUERA_DE_HORARIO, ProtocoloCitas.CAMPO_INICIO);
        }
    }

    /** RN-01 con la operación {@code comprobar(dni)} de CU12 (DEC-064). */
    private void comprobarPaciente(String dni) {
        EstadoPaciente estado;
        try {
            estado = pacientes.comprobar(dni);
        } catch (PacienteException e) {
            throw new CitaException(e.codigo(), e.campo() == null ? null : ProtocoloCitas.CAMPO_DNI);
        }
        if (estado != EstadoPaciente.ACTIVO) {
            throw new CitaException(CodigoError.PACIENTE_DE_BAJA, ProtocoloCitas.CAMPO_DNI);
        }
    }

    /** DEC-044: empieza y termina el mismo día, de lunes a viernes, entre las 08:00 y las 20:00. */
    static boolean dentroDeHorario(Cita cita) {
        LocalDateTime inicio = cita.inicio();
        LocalDateTime fin = cita.fin();
        DayOfWeek dia = inicio.getDayOfWeek();
        return dia != DayOfWeek.SATURDAY && dia != DayOfWeek.SUNDAY
                && !inicio.toLocalTime().isBefore(APERTURA)
                && fin.toLocalDate().equals(inicio.toLocalDate())
                && !fin.toLocalTime().isAfter(CIERRE);
    }

    /** La cita existe, está PROGRAMADA y todavía no ha empezado (RN-08). */
    private Cita modificable(long id) {
        Cita cita = obtenerCita(id);
        if (cita.estado() != EstadoCita.PROGRAMADA || !cita.inicio().isAfter(LocalDateTime.now(reloj))) {
            throw new CitaException(CodigoError.CITA_NO_MODIFICABLE);
        }
        return cita;
    }

    /** Si la cita ya no está PROGRAMADA (por ejemplo, otra petición la ha cambiado), no se toca. */
    private Cita cambiarEstado(long id, EstadoCita nuevo, String motivo) {
        if (!dao.cambiarEstadoSiProgramada(id, nuevo, motivo)) {
            throw new CitaException(CodigoError.CITA_NO_MODIFICABLE);
        }
        return obtenerCita(id);
    }

    private void comprobarRecurso(int idRecurso) {
        if (dao.buscarRecurso(idRecurso).isEmpty()) {
            throw noValido(ProtocoloCitas.CAMPO_RECURSO);
        }
    }

    private static String dniObligatorio(String dni) {
        String normalizado = Paciente.normalizarDni(dni);
        if (normalizado.isEmpty()) {
            throw new CitaException(CodigoError.CAMPO_OBLIGATORIO, ProtocoloCitas.CAMPO_DNI);
        }
        if (!ValidadorPaciente.dniValido(normalizado)) {
            throw noValido(ProtocoloCitas.CAMPO_DNI);
        }
        return normalizado;
    }

    /** DEC-046: entre 1 y 255 caracteres, sin contar los espacios de los extremos. */
    private static String motivoValido(String motivo) {
        String m = motivo == null ? "" : motivo.strip();
        if (m.isEmpty()) {
            throw new CitaException(CodigoError.CAMPO_OBLIGATORIO, ProtocoloCitas.CAMPO_MOTIVO);
        }
        if (m.length() > MAX_MOTIVO) {
            throw noValido(ProtocoloCitas.CAMPO_MOTIVO);
        }
        return m;
    }

    private static <T> T obligatorio(T valor, String campo) {
        if (valor == null) {
            throw new CitaException(CodigoError.CAMPO_OBLIGATORIO, campo);
        }
        return valor;
    }

    private static CitaException noValido(String campo) {
        return new CitaException(CodigoError.CAMPO_NO_VALIDO, campo);
    }
}
