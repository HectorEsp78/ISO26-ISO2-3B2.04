package es.uclm.esi.iso2.servidor.red;

import es.uclm.esi.iso2.comun.dominio.DatosCita;
import es.uclm.esi.iso2.comun.dominio.EstadoCita;
import es.uclm.esi.iso2.comun.protocolo.CodigoError;
import es.uclm.esi.iso2.comun.protocolo.ProtocoloCitas;
import es.uclm.esi.iso2.servidor.citas.CitaException;
import es.uclm.esi.iso2.servidor.citas.ICitaService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Operaciones del protocolo de CU13 (DEC-065): convierten los campos recibidos en llamadas a
 * {@link ICitaService} y su resultado en la línea de respuesta.
 *
 * <p>Aquí solo se comprueba el formato (un número es un número, una fecha es una fecha). Un
 * campo vacío llega al servicio como {@code null}, y es el servicio quien decide si es
 * obligatorio.</p>
 */
public final class OperacionesCitas {

    private OperacionesCitas() {
    }

    /** Registra en el manejador las operaciones de citas. */
    public static void registrar(ManejadorPeticiones manejador, ICitaService servicio) {
        manejador.registrar(ProtocoloCitas.ASIGNAR_CITA, a -> responder(ProtocoloCitas.CAMPOS_DATOS, a,
                () -> ProtocoloCitas.respuestaOk(servicio.asignarCita(new DatosCita(
                        a.get(0),
                        entero(a.get(1), ProtocoloCitas.CAMPO_TIPO_PRUEBA),
                        entero(a.get(2), ProtocoloCitas.CAMPO_RECURSO),
                        fechaHora(a.get(3)),
                        entero(a.get(4), ProtocoloCitas.CAMPO_DURACION))))));

        manejador.registrar(ProtocoloCitas.BUSCAR_CITAS, a -> responder(4, a,
                () -> ProtocoloCitas.respuestaCitas(servicio.buscarCitas(
                        a.get(0),
                        fecha(a.get(1), ProtocoloCitas.CAMPO_DESDE),
                        fecha(a.get(2), ProtocoloCitas.CAMPO_HASTA),
                        estado(a.get(3))))));

        manejador.registrar(ProtocoloCitas.REPROGRAMAR_CITA, a -> responder(4, a,
                () -> ProtocoloCitas.respuestaOk(servicio.reprogramarCita(
                        id(a.get(0)),
                        entero(a.get(1), ProtocoloCitas.CAMPO_RECURSO),
                        fechaHora(a.get(2)),
                        entero(a.get(3), ProtocoloCitas.CAMPO_DURACION)))));

        manejador.registrar(ProtocoloCitas.CANCELAR_CITA, a -> responder(2, a,
                () -> ProtocoloCitas.respuestaOk(servicio.cancelarCita(id(a.get(0)), a.get(1)))));

        manejador.registrar(ProtocoloCitas.LISTAR_TIPOS_PRUEBA, a -> responder(0, a,
                () -> ProtocoloCitas.respuestaTipos(servicio.listarTiposPrueba())));

        manejador.registrar(ProtocoloCitas.LISTAR_RECURSOS, a -> responder(0, a,
                () -> ProtocoloCitas.respuestaRecursos(servicio.listarRecursos())));
    }

    /**
     * Comprueba el número de argumentos, ejecuta la operación y convierte una
     * {@link CitaException} en {@code ERROR;<código>;<detalle>}.
     *
     * @throws IllegalArgumentException si el número de argumentos no es el esperado (el manejador
     *                                  responde {@code PETICION_NO_VALIDA})
     */
    private static String responder(int argumentos, List<String> a, Supplier<String> operacion) {
        if (a.size() != argumentos) {
            throw new IllegalArgumentException("Se esperaban " + argumentos + " argumentos y hay " + a.size());
        }
        try {
            return operacion.get();
        } catch (CitaException e) {
            return ProtocoloCitas.respuestaError(e.codigo(), e.detalle());
        }
    }

    // ---- Conversión de campos ----

    private static Integer entero(String texto, String campo) {
        return convertir(texto, campo, Integer::valueOf);
    }

    private static long id(String texto) {
        Long id = convertir(texto, ProtocoloCitas.CAMPO_ID, Long::valueOf);
        if (id == null) {
            throw new CitaException(CodigoError.CAMPO_OBLIGATORIO, ProtocoloCitas.CAMPO_ID);
        }
        return id;
    }

    private static LocalDateTime fechaHora(String texto) {
        return convertir(texto, ProtocoloCitas.CAMPO_INICIO, LocalDateTime::parse);
    }

    private static LocalDate fecha(String texto, String campo) {
        return convertir(texto, campo, LocalDate::parse);
    }

    private static EstadoCita estado(String texto) {
        return convertir(texto, ProtocoloCitas.CAMPO_ESTADO, EstadoCita::valueOf);
    }

    /** Campo vacío → {@code null}; mal formado → {@code CAMPO_NO_VALIDO} con el nombre del campo. */
    private static <T> T convertir(String texto, String campo, Function<String, T> conversion) {
        String limpio = texto.strip();
        if (limpio.isEmpty()) {
            return null;
        }
        try {
            return conversion.apply(limpio);
        } catch (IllegalArgumentException | DateTimeParseException e) {
            throw new CitaException(CodigoError.CAMPO_NO_VALIDO, campo);
        }
    }
}
