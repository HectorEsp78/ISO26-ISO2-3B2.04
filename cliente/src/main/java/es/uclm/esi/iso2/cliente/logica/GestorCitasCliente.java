package es.uclm.esi.iso2.cliente.logica;

import es.uclm.esi.iso2.cliente.servicio.ExcepcionServicioCitas;
import es.uclm.esi.iso2.cliente.servicio.ServicioCitas;
import es.uclm.esi.iso2.comun.dominio.Cita;
import es.uclm.esi.iso2.comun.dominio.DatosCita;
import es.uclm.esi.iso2.comun.dominio.EstadoCita;
import es.uclm.esi.iso2.comun.dominio.Paciente;
import es.uclm.esi.iso2.comun.dominio.ValidadorPaciente;
import es.uclm.esi.iso2.comun.protocolo.CodigoError;
import es.uclm.esi.iso2.comun.protocolo.ProtocoloCitas;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Clase de control del cliente para CU13 (como {@link GestorPacientesCliente} en CU12, DEC-059).
 *
 * <p>Comprueba lo que ha escrito el usuario (que no falte nada y que el formato sea correcto), llama
 * al {@link ServicioCitas} y traduce el resultado a mensajes. Las reglas de negocio (paciente
 * activo, fecha futura, horario, solapes) las decide el servidor, que es quien tiene los datos.
 * Las pantallas JavaFX solo muestran lo que devuelve esta clase, así que se puede probar con JUnit.</p>
 */
public class GestorCitasCliente {

    /** Campos del formulario que no existen en el protocolo: allí van juntos en {@code inicio}. */
    public static final String CAMPO_FECHA = "fecha";
    public static final String CAMPO_HORA = "hora";
    /** Longitud máxima del motivo de cancelación (DEC-046). */
    public static final int MAX_MOTIVO = 255;

    private final ServicioCitas servicio;
    private final Clock reloj;
    private Catalogo catalogo = Catalogo.VACIO;

    public GestorCitasCliente(ServicioCitas servicio) {
        this(servicio, Clock.systemDefaultZone());
    }

    /** Permite fijar el momento actual en las pruebas. */
    public GestorCitasCliente(ServicioCitas servicio, Clock reloj) {
        this.servicio = servicio;
        this.reloj = reloj;
    }

    /** Catálogo cargado con {@link #cargarCatalogo()}, o vacío si todavía no se ha cargado. */
    public Catalogo catalogo() {
        return catalogo;
    }

    /** Pide al servidor los tipos de prueba y los recursos para los desplegables. */
    public ResultadoCitas<Catalogo> cargarCatalogo() {
        return llamar(() -> new Catalogo(servicio.listarTiposPrueba(), servicio.listarRecursos()), c -> {
            catalogo = c;
            return "";
        });
    }

    /** Flujo básico: asigna una cita nueva. */
    public ResultadoCitas<Cita> asignar(EntradaCita entrada) {
        List<String> errores = new ArrayList<>();
        String dni = Paciente.normalizarDni(entrada.dni());
        if (dni.isEmpty() || !ValidadorPaciente.dniValido(dni)) {
            errores.add(ProtocoloCitas.CAMPO_DNI);
        }
        if (entrada.tipo() == null) {
            errores.add(ProtocoloCitas.CAMPO_TIPO_PRUEBA);
        }
        Formulario f = leerFormulario(entrada, errores);
        if (!errores.isEmpty()) {
            return errorDeValidacion(errores, entrada);
        }
        DatosCita datos = new DatosCita(dni, entrada.tipo().id(), entrada.recurso().id(), f.inicio(), f.duracion());
        return llamar(() -> servicio.asignar(datos),
                c -> "Cita nº " + c.id() + " asignada a " + c.dniPaciente() + " el " + cuando(c) + " ("
                        + catalogo.nombreTipo(c.idTipoPrueba()) + ", " + catalogo.nombreRecurso(c.idRecurso())
                        + ").");
    }

    /**
     * Flujo alternativo de reprogramación: cambia el recurso, la fecha y hora y/o la duración de la
     * misma cita (DEC-045). El paciente y el tipo de prueba no cambian, así que se ignoran.
     */
    public ResultadoCitas<Cita> reprogramar(Cita actual, EntradaCita entrada) {
        if (!puedeModificar(actual)) {
            return ResultadoCitas.error(MensajesCitas.mensaje(CodigoError.CITA_NO_MODIFICABLE, null), List.of());
        }
        List<String> errores = new ArrayList<>();
        Formulario f = leerFormulario(entrada, errores);
        if (!errores.isEmpty()) {
            return errorDeValidacion(errores, entrada);
        }
        return llamar(() -> servicio.reprogramar(actual.id(), entrada.recurso().id(), f.inicio(), f.duracion()),
                c -> "Cita nº " + c.id() + " reprogramada al " + cuando(c) + " ("
                        + catalogo.nombreRecurso(c.idRecurso()) + ").");
    }

    /** Flujo alternativo de cancelación, después de que el usuario lo haya confirmado (DEC-046). */
    public ResultadoCitas<Cita> cancelar(Cita cita, String motivo) {
        if (!puedeModificar(cita)) {
            return ResultadoCitas.error(MensajesCitas.mensaje(CodigoError.CITA_NO_MODIFICABLE, null), List.of());
        }
        String m = motivo == null ? "" : motivo.strip();
        if (m.isEmpty()) {
            return error(CodigoError.CAMPO_OBLIGATORIO, ProtocoloCitas.CAMPO_MOTIVO);
        }
        if (m.length() > MAX_MOTIVO) {
            return error(CodigoError.CAMPO_NO_VALIDO, ProtocoloCitas.CAMPO_MOTIVO);
        }
        return llamar(() -> servicio.cancelar(cita.id(), m), c -> "Cita nº " + c.id() + " cancelada.");
    }

    /**
     * Consulta de citas. Todos los filtros son opcionales.
     *
     * @param desde fecha escrita como {@code dd/mm/aaaa}, o vacío
     * @param hasta fecha escrita como {@code dd/mm/aaaa}, o vacío
     * @param estado estado, o {@code null} para todos
     */
    public ResultadoCitas<List<Cita>> buscar(String dni, String desde, String hasta, EstadoCita estado) {
        List<String> errores = new ArrayList<>();
        String normalizado = Paciente.normalizarDni(dni);
        if (!normalizado.isEmpty() && !ValidadorPaciente.dniValido(normalizado)) {
            errores.add(ProtocoloCitas.CAMPO_DNI);
        }
        LocalDate fechaDesde = fechaOpcional(desde, ProtocoloCitas.CAMPO_DESDE, errores);
        LocalDate fechaHasta = fechaOpcional(hasta, ProtocoloCitas.CAMPO_HASTA, errores);
        if (fechaDesde != null && fechaHasta != null && fechaHasta.isBefore(fechaDesde)) {
            errores.add(ProtocoloCitas.CAMPO_HASTA);
        }
        if (!errores.isEmpty()) {
            return errorDeValidacion(errores, null);
        }
        String filtroDni = normalizado.isEmpty() ? null : normalizado;
        return llamar(() -> servicio.buscar(filtroDni, fechaDesde, fechaHasta, estado), lista -> switch (lista.size()) {
            case 0 -> "No hay ninguna cita con esos filtros.";
            case 1 -> "Se ha encontrado 1 cita.";
            default -> "Se han encontrado " + lista.size() + " citas.";
        });
    }

    /** Solo se pueden reprogramar o cancelar las citas PROGRAMADA que aún no han empezado (RN-08). */
    public boolean puedeModificar(Cita cita) {
        return cita != null && cita.estado() == EstadoCita.PROGRAMADA
                && cita.inicio().isAfter(LocalDateTime.now(reloj));
    }

    /** Pregunta que se muestra antes de cancelar. */
    public String preguntaConfirmacionCancelar(Cita cita) {
        return "¿Seguro que quieres cancelar la cita nº " + cita.id() + " de " + cita.dniPaciente() + " ("
                + catalogo.nombreTipo(cita.idTipoPrueba()) + ") del " + cuando(cita)
                + "? La cita no se borra: queda cancelada con el motivo que escribas y el hueco queda libre.";
    }

    // ---- Lectura del formulario ----

    /** Inicio y duración ya convertidos (pueden ser {@code null} si había errores). */
    private record Formulario(LocalDateTime inicio, Integer duracion) {
    }

    /** Comprueba recurso, fecha, hora y duración, y añade a {@code errores} los campos que fallan. */
    private static Formulario leerFormulario(EntradaCita entrada, List<String> errores) {
        if (entrada.recurso() == null) {
            errores.add(ProtocoloCitas.CAMPO_RECURSO);
        }
        LocalDate fecha = fechaObligatoria(entrada.fecha(), errores);
        LocalTime hora = horaObligatoria(entrada.hora(), errores);
        Integer duracion = duracionOpcional(entrada.duracion(), errores);
        LocalDateTime inicio = fecha == null || hora == null ? null : fecha.atTime(hora);
        return new Formulario(inicio, duracion);
    }

    private static LocalDate fechaObligatoria(String texto, List<String> errores) {
        if (vacio(texto)) {
            errores.add(CAMPO_FECHA);
            return null;
        }
        return fechaOpcional(texto, CAMPO_FECHA, errores);
    }

    private static LocalDate fechaOpcional(String texto, String campo, List<String> errores) {
        if (vacio(texto)) {
            return null;
        }
        try {
            return LocalDate.parse(texto.strip(), Formatos.FECHA_PANTALLA);
        } catch (DateTimeParseException e) {
            errores.add(campo);
            return null;
        }
    }

    private static LocalTime horaObligatoria(String texto, List<String> errores) {
        if (vacio(texto)) {
            errores.add(CAMPO_HORA);
            return null;
        }
        try {
            return LocalTime.parse(texto.strip(), FormatosCitas.HORA_PANTALLA);
        } catch (DateTimeParseException e) {
            errores.add(CAMPO_HORA);
            return null;
        }
    }

    /** Vacío significa «la duración del tipo de prueba». */
    private static Integer duracionOpcional(String texto, List<String> errores) {
        if (vacio(texto)) {
            return null;
        }
        try {
            int minutos = Integer.parseInt(texto.strip());
            if (minutos > 0) {
                return minutos;
            }
        } catch (NumberFormatException e) {
            // se marca abajo como no válido
        }
        errores.add(ProtocoloCitas.CAMPO_DURACION);
        return null;
    }

    private static boolean vacio(String texto) {
        return texto == null || texto.isBlank();
    }

    // ---- Resultados ----

    private <T> ResultadoCitas<T> llamar(Supplier<T> operacion, Function<T, String> mensajeExito) {
        try {
            T valor = operacion.get();
            return ResultadoCitas.correcto(valor, mensajeExito.apply(valor));
        } catch (ExcepcionServicioCitas e) {
            if (e.codigo() == CodigoError.SIN_CONEXION) {
                return ResultadoCitas.sinConexion(MensajesCitas.mensaje(e.codigo(), null));
            }
            return error(e.codigo(), e.detalle());
        }
    }

    private static <T> ResultadoCitas<T> error(CodigoError codigo, String detalle) {
        return ResultadoCitas.error(MensajesCitas.mensaje(codigo, detalle), camposAfectados(codigo, detalle));
    }

    /**
     * Campos del formulario que hay que señalar para un error del servidor. El servidor habla de
     * {@code inicio}, que en pantalla son dos campos: fecha y hora.
     */
    static List<String> camposAfectados(CodigoError codigo, String detalle) {
        return switch (codigo) {
            case CAMPO_OBLIGATORIO, CAMPO_NO_VALIDO -> detalle == null ? List.of() : enPantalla(detalle);
            case PACIENTE_NO_ENCONTRADO, PACIENTE_DE_BAJA -> List.of(ProtocoloCitas.CAMPO_DNI);
            case FECHA_PASADA, FUERA_DE_HORARIO, SOLAPE_PACIENTE -> List.of(CAMPO_FECHA, CAMPO_HORA);
            case SOLAPE_RECURSO -> List.of(ProtocoloCitas.CAMPO_RECURSO, CAMPO_FECHA, CAMPO_HORA);
            default -> List.of();
        };
    }

    private static List<String> enPantalla(String campo) {
        return ProtocoloCitas.CAMPO_INICIO.equals(campo) ? List.of(CAMPO_FECHA, CAMPO_HORA) : List.of(campo);
    }

    /** Mensaje con el primer error y la lista de todos los campos que hay que señalar. */
    private static <T> ResultadoCitas<T> errorDeValidacion(List<String> errores, EntradaCita entrada) {
        String primero = errores.get(0);
        boolean falta = esObligatorioVacio(primero, entrada);
        String mensaje = MensajesCitas.mensaje(falta ? CodigoError.CAMPO_OBLIGATORIO : CodigoError.CAMPO_NO_VALIDO,
                primero);
        if (errores.size() > 1) {
            mensaje += " Revisa también los demás campos marcados.";
        }
        return ResultadoCitas.error(mensaje, errores);
    }

    /** Indica si el campo que falla es obligatorio y está vacío (y no mal escrito). */
    private static boolean esObligatorioVacio(String campo, EntradaCita entrada) {
        if (entrada == null) {
            return false;
        }
        return switch (campo) {
            case ProtocoloCitas.CAMPO_DNI -> vacio(entrada.dni());
            case ProtocoloCitas.CAMPO_TIPO_PRUEBA -> entrada.tipo() == null;
            case ProtocoloCitas.CAMPO_RECURSO -> entrada.recurso() == null;
            case CAMPO_FECHA -> vacio(entrada.fecha());
            case CAMPO_HORA -> vacio(entrada.hora());
            default -> false;
        };
    }

    private static String cuando(Cita c) {
        return FormatosCitas.fecha(c.inicio().toLocalDate()) + " a las " + FormatosCitas.hora(c.inicio().toLocalTime());
    }
}
