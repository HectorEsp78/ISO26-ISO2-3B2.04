package es.uclm.esi.iso2.cliente.servicio;

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

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Servicio de citas sin red ni base de datos (como {@link ServicioPacientesEnMemoria}, DEC-059).
 *
 * <p>Aplica las mismas reglas y devuelve los mismos errores que {@code CitaService} del servidor
 * (DEC-063 y DEC-065), así que sirve para las pruebas y para usar la pantalla sin servidor. Los
 * datos se pierden al cerrar el cliente.</p>
 */
public class ServicioCitasEnMemoria implements ServicioCitas {

    /** Horario provisional de la DEC-044: de lunes a viernes, de 08:00 a 20:00. */
    public static final LocalTime APERTURA = LocalTime.of(8, 0);
    public static final LocalTime CIERRE = LocalTime.of(20, 0);
    /** Longitud máxima del motivo de cancelación (DEC-046). */
    public static final int MAX_MOTIVO = 255;

    /** Catálogo de ejemplo: el mismo que carga el script SQL del servidor (DEC-047). */
    public static final List<TipoPrueba> TIPOS_EJEMPLO = List.of(
            new TipoPrueba(1, "Radiografía de tórax", 15),
            new TipoPrueba(2, "Ecografía abdominal", 30),
            new TipoPrueba(3, "Mamografía", 20),
            new TipoPrueba(4, "TAC craneal", 30),
            new TipoPrueba(5, "Resonancia magnética de rodilla", 45));
    public static final List<Recurso> RECURSOS_EJEMPLO = List.of(
            new Recurso(1, "Sala de rayos X 1"),
            new Recurso(2, "Ecógrafo 1"),
            new Recurso(3, "Mamógrafo 1"),
            new Recurso(4, "TAC 1"),
            new Recurso(5, "Resonancia 1"));

    private final Map<Long, Cita> citas = new LinkedHashMap<>();
    private final Map<String, EstadoPaciente> pacientes;
    private final Clock reloj;
    private long ultimoId;

    /**
     * @param pacientes DNI y estado de los pacientes que existen (hace el papel de {@code comprobar(dni)} de CU12)
     * @param reloj     momento actual (se puede fijar en las pruebas)
     */
    public ServicioCitasEnMemoria(Map<String, EstadoPaciente> pacientes, Clock reloj) {
        this.pacientes = new HashMap<>(pacientes);
        this.reloj = reloj;
    }

    /**
     * Servicio con los pacientes de ejemplo de {@link ServicioPacientesEnMemoria#conDatosDeEjemplo()}
     * y tres citas en los próximos días laborables, para probar la pantalla.
     */
    public static ServicioCitasEnMemoria conDatosDeEjemplo() {
        return conDatosDeEjemplo(Clock.systemDefaultZone());
    }

    static ServicioCitasEnMemoria conDatosDeEjemplo(Clock reloj) {
        ServicioCitasEnMemoria servicio = new ServicioCitasEnMemoria(Map.of(
                "12345678Z", EstadoPaciente.ACTIVO,
                "X1234567L", EstadoPaciente.ACTIVO,
                "87654321X", EstadoPaciente.BAJA), reloj);
        LocalDate dia = siguienteDiaLaborable(LocalDate.now(reloj));
        servicio.asignar(new DatosCita("12345678Z", 1, 1, dia.atTime(9, 0), null));
        servicio.asignar(new DatosCita("X1234567L", 2, 2, dia.atTime(10, 30), null));
        Cita cancelada = servicio.asignar(new DatosCita("12345678Z", 4, 4,
                siguienteDiaLaborable(dia).atTime(12, 0), null));
        servicio.cancelar(cancelada.id(), "El paciente no puede venir ese día");
        return servicio;
    }

    /** Primer día de lunes a viernes posterior al indicado. */
    static LocalDate siguienteDiaLaborable(LocalDate desde) {
        LocalDate dia = desde.plusDays(1);
        while (dia.getDayOfWeek() == DayOfWeek.SATURDAY || dia.getDayOfWeek() == DayOfWeek.SUNDAY) {
            dia = dia.plusDays(1);
        }
        return dia;
    }

    @Override
    public synchronized Cita asignar(DatosCita datos) {
        String dni = dniObligatorio(datos.dniPaciente());
        Integer idTipo = obligatorio(datos.idTipoPrueba(), ProtocoloCitas.CAMPO_TIPO_PRUEBA);
        Integer idRecurso = obligatorio(datos.idRecurso(), ProtocoloCitas.CAMPO_RECURSO);
        LocalDateTime inicio = obligatorio(datos.inicio(), ProtocoloCitas.CAMPO_INICIO);

        TipoPrueba tipo = TIPOS_EJEMPLO.stream().filter(t -> t.id() == idTipo).findFirst()
                .orElseThrow(() -> noValido(ProtocoloCitas.CAMPO_TIPO_PRUEBA));
        comprobarRecurso(idRecurso);
        int duracion = datos.duracionMinutos() == null ? tipo.duracionMinutos() : datos.duracionMinutos();

        Cita nueva = new Cita(0, dni, idTipo, idRecurso, inicio, duracion, EstadoCita.PROGRAMADA, "");
        comprobarCita(nueva);
        comprobarSolapes(nueva);
        Cita guardada = nueva.conId(++ultimoId);
        citas.put(guardada.id(), guardada);
        return guardada;
    }

    @Override
    public synchronized List<Cita> buscar(String dni, LocalDate desde, LocalDate hasta, EstadoCita estado) {
        if (desde != null && hasta != null && hasta.isBefore(desde)) {
            throw noValido(ProtocoloCitas.CAMPO_HASTA);
        }
        String normalizado = Paciente.normalizarDni(dni);
        List<Cita> encontradas = new ArrayList<>();
        for (Cita c : citas.values()) {
            LocalDate dia = c.inicio().toLocalDate();
            if ((normalizado.isEmpty() || c.dniPaciente().equals(normalizado))
                    && (desde == null || !dia.isBefore(desde))
                    && (hasta == null || !dia.isAfter(hasta))
                    && (estado == null || c.estado() == estado)) {
                encontradas.add(c);
            }
        }
        encontradas.sort(Comparator.comparing(Cita::inicio).thenComparingLong(Cita::id));
        return encontradas;
    }

    @Override
    public synchronized Cita reprogramar(long id, Integer idRecurso, LocalDateTime inicio, Integer duracionMinutos) {
        Cita actual = modificable(id);
        if (idRecurso != null) {
            comprobarRecurso(idRecurso);
        }
        Cita nueva = new Cita(actual.id(), actual.dniPaciente(), actual.idTipoPrueba(),
                idRecurso == null ? actual.idRecurso() : idRecurso,
                inicio == null ? actual.inicio() : inicio,
                duracionMinutos == null ? actual.duracionMinutos() : duracionMinutos,
                EstadoCita.PROGRAMADA, "");
        comprobarCita(nueva);
        comprobarSolapes(nueva);
        citas.put(id, nueva);
        return nueva;
    }

    @Override
    public synchronized Cita cancelar(long id, String motivo) {
        String m = motivoValido(motivo);
        Cita cancelada = modificable(id).conEstado(EstadoCita.CANCELADA, m);
        citas.put(id, cancelada);
        return cancelada;
    }

    @Override
    public List<TipoPrueba> listarTiposPrueba() {
        return TIPOS_EJEMPLO;
    }

    @Override
    public List<Recurso> listarRecursos() {
        return RECURSOS_EJEMPLO;
    }

    // ---- Reglas (las mismas que CitaService del servidor) ----

    /** Duración, paciente activo (RN-01), fecha futura (RN-02) y horario (RN-03). */
    private void comprobarCita(Cita cita) {
        if (cita.duracionMinutos() <= 0) {
            throw noValido(ProtocoloCitas.CAMPO_DURACION);
        }
        EstadoPaciente estado = pacientes.get(cita.dniPaciente());
        if (estado == null) {
            throw new ExcepcionServicioCitas(CodigoError.PACIENTE_NO_ENCONTRADO);
        }
        if (estado != EstadoPaciente.ACTIVO) {
            throw new ExcepcionServicioCitas(CodigoError.PACIENTE_DE_BAJA, ProtocoloCitas.CAMPO_DNI);
        }
        if (!cita.inicio().isAfter(LocalDateTime.now(reloj))) {
            throw new ExcepcionServicioCitas(CodigoError.FECHA_PASADA, ProtocoloCitas.CAMPO_INICIO);
        }
        if (!dentroDeHorario(cita)) {
            throw new ExcepcionServicioCitas(CodigoError.FUERA_DE_HORARIO, ProtocoloCitas.CAMPO_INICIO);
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

    /** RN-04 a RN-07: solo cuentan las citas PROGRAMADA y, al reprogramar, no cuenta la propia cita. */
    private void comprobarSolapes(Cita nueva) {
        Optional<Cita> conRecurso = primeraQueSolapa(nueva, c -> c.idRecurso() == nueva.idRecurso());
        if (conRecurso.isPresent()) {
            throw new ExcepcionServicioCitas(CodigoError.SOLAPE_RECURSO, String.valueOf(conRecurso.get().id()));
        }
        Optional<Cita> conPaciente = primeraQueSolapa(nueva, c -> c.dniPaciente().equals(nueva.dniPaciente()));
        if (conPaciente.isPresent()) {
            throw new ExcepcionServicioCitas(CodigoError.SOLAPE_PACIENTE, String.valueOf(conPaciente.get().id()));
        }
    }

    private Optional<Cita> primeraQueSolapa(Cita nueva, Predicate<Cita> mismo) {
        return citas.values().stream()
                .filter(c -> c.estado() == EstadoCita.PROGRAMADA && c.id() != nueva.id())
                .filter(mismo)
                .filter(nueva::solapaCon)
                .min(Comparator.comparing(Cita::inicio));
    }

    /** La cita existe, está PROGRAMADA y todavía no ha empezado (RN-08). */
    private Cita modificable(long id) {
        Cita cita = citas.get(id);
        if (cita == null) {
            throw new ExcepcionServicioCitas(CodigoError.CITA_NO_ENCONTRADA);
        }
        if (cita.estado() != EstadoCita.PROGRAMADA || !cita.inicio().isAfter(LocalDateTime.now(reloj))) {
            throw new ExcepcionServicioCitas(CodigoError.CITA_NO_MODIFICABLE);
        }
        return cita;
    }

    private static void comprobarRecurso(int idRecurso) {
        if (RECURSOS_EJEMPLO.stream().noneMatch(r -> r.id() == idRecurso)) {
            throw noValido(ProtocoloCitas.CAMPO_RECURSO);
        }
    }

    private static String dniObligatorio(String dni) {
        String normalizado = Paciente.normalizarDni(dni);
        if (normalizado.isEmpty()) {
            throw new ExcepcionServicioCitas(CodigoError.CAMPO_OBLIGATORIO, ProtocoloCitas.CAMPO_DNI);
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
            throw new ExcepcionServicioCitas(CodigoError.CAMPO_OBLIGATORIO, ProtocoloCitas.CAMPO_MOTIVO);
        }
        if (m.length() > MAX_MOTIVO) {
            throw noValido(ProtocoloCitas.CAMPO_MOTIVO);
        }
        return m;
    }

    private static <T> T obligatorio(T valor, String campo) {
        if (valor == null) {
            throw new ExcepcionServicioCitas(CodigoError.CAMPO_OBLIGATORIO, campo);
        }
        return valor;
    }

    private static ExcepcionServicioCitas noValido(String campo) {
        return new ExcepcionServicioCitas(CodigoError.CAMPO_NO_VALIDO, campo);
    }
}
