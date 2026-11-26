package es.uclm.esi.iso2.cliente.logica;

import es.uclm.esi.iso2.cliente.servicio.ExcepcionServicioPruebas;
import es.uclm.esi.iso2.cliente.servicio.ServicioPruebas;
import es.uclm.esi.iso2.comun.dominio.ErrorCampo;
import es.uclm.esi.iso2.comun.dominio.EstadoPrueba;
import es.uclm.esi.iso2.comun.dominio.FiltroPruebas;
import es.uclm.esi.iso2.comun.dominio.Incidencia;
import es.uclm.esi.iso2.comun.dominio.NuevaIncidencia;
import es.uclm.esi.iso2.comun.dominio.Paciente;
import es.uclm.esi.iso2.comun.dominio.Prueba;
import es.uclm.esi.iso2.comun.dominio.ResumenPrueba;
import es.uclm.esi.iso2.comun.dominio.SolicitudPendiente;
import es.uclm.esi.iso2.comun.dominio.TipoIncidencia;
import es.uclm.esi.iso2.comun.dominio.ValidadorPaciente;
import es.uclm.esi.iso2.comun.dominio.ValidadorPrueba;
import es.uclm.esi.iso2.comun.protocolo.CodigoError;
import es.uclm.esi.iso2.comun.protocolo.ProtocoloPruebas;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Clase de control del cliente para CU10 (DEC-080).
 *
 * <p>Convierte lo que escribe el médico en valores, los valida antes de enviarlos (con las mismas
 * reglas que el servidor), llama al {@link ServicioPruebas} y traduce el resultado a mensajes.
 * Las pantallas JavaFX solo muestran lo que devuelve esta clase.</p>
 */
public class GestorPruebasCliente {

    private final ServicioPruebas servicio;
    private final String idMedico;
    private final Clock reloj;
    private final ValidadorPaciente validadorPaciente = new ValidadorPaciente();

    public GestorPruebasCliente(ServicioPruebas servicio, String idMedico) {
        this(servicio, idMedico, Clock.systemDefaultZone());
    }

    public GestorPruebasCliente(ServicioPruebas servicio, String idMedico, Clock reloj) {
        this.servicio = servicio;
        this.idMedico = idMedico;
        this.reloj = reloj;
    }

    public String idMedico() {
        return idMedico;
    }

    /** Flujo 6.1: solicitudes del médico pendientes de registrar; el DNI es opcional. */
    public ResultadoPruebas<List<SolicitudPendiente>> listarPendientes(String dni) {
        String filtro = dni == null ? "" : dni.strip();
        if (!filtro.isEmpty() && validadorPaciente.validarDni(filtro).isPresent()) {
            return ResultadoPruebas.error("El DNI/NIE no es válido.", ProtocoloPruebas.CAMPO_DNI);
        }
        return llamar(() -> servicio.listarPendientes(idMedico, Paciente.normalizarDni(filtro)), l -> "");
    }

    /**
     * Flujo 6.2: registra una prueba realizada.
     *
     * @param fechaHoraTexto fecha y hora como {@code dd/mm/aaaa hh:mm}
     */
    public ResultadoPruebas<Prueba> registrarRealizacion(long idSolicitud, String fechaHoraTexto,
            String observaciones, List<NuevaIncidencia> incidencias) {
        Optional<LocalDateTime> fechaHora = FormatosPruebas.fechaHora(fechaHoraTexto);
        if (fechaHora.isEmpty()) {
            CodigoError codigo = fechaHoraTexto == null || fechaHoraTexto.isBlank()
                    ? CodigoError.CAMPO_OBLIGATORIO : CodigoError.CAMPO_NO_VALIDO;
            return errorDeValidacion(new ErrorCampo(ProtocoloPruebas.CAMPO_FECHA_HORA, codigo));
        }
        List<NuevaIncidencia> nuevas = incidencias == null ? List.of() : incidencias;
        Optional<ErrorCampo> error = ValidadorPrueba.validarRealizacion(fechaHora.get(), observaciones, nuevas,
                LocalDateTime.now(reloj));
        if (error.isPresent()) {
            return errorDeValidacion(error.get());
        }
        return llamar(() -> servicio.registrarRealizacion(idMedico, idSolicitud, fechaHora.get(),
                ValidadorPrueba.normalizar(observaciones), nuevas), p -> "Prueba registrada como realizada.");
    }

    /** Flujo 6.3: registra que la prueba no se ha realizado. */
    public ResultadoPruebas<Prueba> registrarCancelacion(long idSolicitud, String motivo) {
        Optional<ErrorCampo> error = ValidadorPrueba.validarMotivo(motivo);
        if (error.isPresent()) {
            return errorDeValidacion(error.get());
        }
        return llamar(() -> servicio.registrarCancelacion(idMedico, idSolicitud, ValidadorPrueba.normalizar(motivo)),
                p -> "Prueba registrada como cancelada.");
    }

    /** Flujo 6.5: añade una incidencia a una prueba ya registrada. */
    public ResultadoPruebas<Incidencia> anadirIncidencia(long idPrueba, TipoIncidencia tipo, String descripcion) {
        NuevaIncidencia nueva = new NuevaIncidencia(tipo, descripcion);
        Optional<ErrorCampo> error = ValidadorPrueba.validarIncidencia(nueva);
        if (error.isPresent()) {
            return errorDeValidacion(error.get());
        }
        return llamar(() -> servicio.anadirIncidencia(idMedico, idPrueba,
                new NuevaIncidencia(tipo, ValidadorPrueba.normalizar(descripcion))), i -> "Incidencia añadida.");
    }

    /**
     * Flujo 6.4: busca pruebas del médico. Todos los filtros son opcionales y vacíos significan
     * «sin filtrar»; las fechas se escriben {@code dd/mm/aaaa}.
     */
    public ResultadoPruebas<List<ResumenPrueba>> buscar(String dni, String idSolicitudTexto, String desdeTexto,
            String hastaTexto, EstadoPrueba estado) {
        String dniLimpio = dni == null ? "" : dni.strip();
        if (!dniLimpio.isEmpty() && validadorPaciente.validarDni(dniLimpio).isPresent()) {
            return ResultadoPruebas.error("El DNI/NIE no es válido.", ProtocoloPruebas.CAMPO_DNI);
        }
        Long idSolicitud = null;
        if (idSolicitudTexto != null && !idSolicitudTexto.isBlank()) {
            try {
                idSolicitud = Long.valueOf(idSolicitudTexto.strip());
            } catch (NumberFormatException e) {
                return errorDeValidacion(new ErrorCampo(ProtocoloPruebas.CAMPO_ID_SOLICITUD,
                        CodigoError.CAMPO_NO_VALIDO));
            }
        }
        Optional<LocalDate> desde = FormatosPruebas.fecha(desdeTexto);
        if (desde.isEmpty() && desdeTexto != null && !desdeTexto.isBlank()) {
            return errorDeValidacion(new ErrorCampo(ProtocoloPruebas.CAMPO_DESDE, CodigoError.CAMPO_NO_VALIDO));
        }
        Optional<LocalDate> hasta = FormatosPruebas.fecha(hastaTexto);
        if (hasta.isEmpty() && hastaTexto != null && !hastaTexto.isBlank()) {
            return errorDeValidacion(new ErrorCampo(ProtocoloPruebas.CAMPO_HASTA, CodigoError.CAMPO_NO_VALIDO));
        }
        FiltroPruebas filtro = new FiltroPruebas(Paciente.normalizarDni(dniLimpio), idSolicitud,
                desde.orElse(null), hasta.orElse(null), estado);
        Optional<ErrorCampo> error = ValidadorPrueba.validarFiltro(filtro);
        if (error.isPresent()) {
            return errorDeValidacion(error.get());
        }
        return llamar(() -> servicio.buscar(idMedico, filtro),
                l -> l.isEmpty() ? "No hay pruebas que cumplan los filtros." : "");
    }

    /** Flujo 6.6: prueba completa con sus incidencias. */
    public ResultadoPruebas<Prueba> obtener(long idPrueba) {
        return llamar(() -> servicio.obtener(idMedico, idPrueba), p -> "");
    }

    // ---- Auxiliares ----

    private <T> ResultadoPruebas<T> llamar(Supplier<T> operacion, Function<T, String> mensajeExito) {
        try {
            T resultado = operacion.get();
            return ResultadoPruebas.correcto(resultado, mensajeExito.apply(resultado));
        } catch (ExcepcionServicioPruebas e) {
            String mensaje = MensajesError.mensaje(e.codigo(), null);
            if (e.codigo() == CodigoError.SIN_CONEXION) {
                return ResultadoPruebas.sinConexion(mensaje);
            }
            return ResultadoPruebas.error(mensajeConDetalle(e, mensaje), campoDe(e));
        }
    }

    private static String mensajeConDetalle(ExcepcionServicioPruebas e, String mensaje) {
        if (e.codigo() == CodigoError.PRUEBA_YA_EXISTE && e.detalle() != null) {
            return mensaje + " Es la prueba número " + e.detalle() + ".";
        }
        return mensaje;
    }

    /** Solo los errores de campo llevan en el detalle el nombre de un campo. */
    private static String campoDe(ExcepcionServicioPruebas e) {
        boolean deCampo = e.codigo() == CodigoError.CAMPO_OBLIGATORIO || e.codigo() == CodigoError.CAMPO_NO_VALIDO
                || e.codigo() == CodigoError.FECHA_FUTURA;
        return deCampo ? e.detalle() : null;
    }

    private static <T> ResultadoPruebas<T> errorDeValidacion(ErrorCampo error) {
        return ResultadoPruebas.error(MensajesPruebas.mensajeDeCampo(error.codigo(), error.campo()), error.campo());
    }
}
