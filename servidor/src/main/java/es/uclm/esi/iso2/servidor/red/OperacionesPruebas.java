package es.uclm.esi.iso2.servidor.red;

import es.uclm.esi.iso2.comun.dominio.EstadoPrueba;
import es.uclm.esi.iso2.comun.dominio.FiltroPruebas;
import es.uclm.esi.iso2.comun.dominio.NuevaIncidencia;
import es.uclm.esi.iso2.comun.dominio.TipoIncidencia;
import es.uclm.esi.iso2.comun.protocolo.CodigoError;
import es.uclm.esi.iso2.comun.protocolo.Protocolo;
import es.uclm.esi.iso2.comun.protocolo.ProtocoloPruebas;
import es.uclm.esi.iso2.servidor.pruebas.IPruebaService;
import es.uclm.esi.iso2.servidor.pruebas.PruebaException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Operaciones del protocolo de CU10 (DEC-078): convierten los campos recibidos en llamadas a
 * {@link IPruebaService} y su resultado en la línea de respuesta.
 *
 * <p>Aquí solo se comprueba el formato. Un campo vacío llega al servicio como {@code null}, y es
 * el servicio quien decide si es obligatorio.</p>
 */
public final class OperacionesPruebas {

    private OperacionesPruebas() {
    }

    /** Registra en el manejador las operaciones de pruebas. */
    public static void registrar(ManejadorPeticiones manejador, IPruebaService servicio) {
        manejador.registrar(ProtocoloPruebas.LISTAR_SOLICITUDES_PENDIENTES, a -> responder(2, a,
                () -> ProtocoloPruebas.respuestaPendientes(servicio.buscarSolicitudesPendientes(a.get(0),
                        a.get(1)))));

        manejador.registrar(ProtocoloPruebas.REGISTRAR_PRUEBA, a -> {
            if (a.size() < 5) {
                throw new IllegalArgumentException("Faltan argumentos");
            }
            return responder(5 + incidenciasEsperadas(a), a, () -> ProtocoloPruebas.respuestaOk(
                    servicio.registrarRealizacion(a.get(0), idObligatorio(a.get(1), ProtocoloPruebas.CAMPO_ID_SOLICITUD),
                            fechaHora(a.get(2)), a.get(3), incidencias(a, 5))));
        });

        manejador.registrar(ProtocoloPruebas.CANCELAR_PRUEBA, a -> responder(3, a,
                () -> ProtocoloPruebas.respuestaOk(servicio.registrarCancelacion(a.get(0),
                        idObligatorio(a.get(1), ProtocoloPruebas.CAMPO_ID_SOLICITUD), a.get(2)))));

        manejador.registrar(ProtocoloPruebas.ANADIR_INCIDENCIA, a -> responder(4, a,
                () -> ProtocoloPruebas.respuestaOk(servicio.anadirIncidencia(a.get(0),
                        idObligatorio(a.get(1), ProtocoloPruebas.CAMPO_ID_PRUEBA), incidencia(a.get(2), a.get(3))))));

        manejador.registrar(ProtocoloPruebas.BUSCAR_PRUEBAS, a -> responder(6, a,
                () -> ProtocoloPruebas.respuestaResumenes(servicio.buscarPruebas(a.get(0), new FiltroPruebas(
                        a.get(1),
                        convertir(a.get(2), ProtocoloPruebas.CAMPO_ID_SOLICITUD, Long::valueOf),
                        convertir(a.get(3), ProtocoloPruebas.CAMPO_DESDE, LocalDate::parse),
                        convertir(a.get(4), ProtocoloPruebas.CAMPO_HASTA, LocalDate::parse),
                        convertir(a.get(5), ProtocoloPruebas.CAMPO_ESTADO, EstadoPrueba::valueOf))))));

        manejador.registrar(ProtocoloPruebas.OBTENER_PRUEBA, a -> responder(2, a,
                () -> ProtocoloPruebas.respuestaOk(servicio.consultarPrueba(a.get(0),
                        idObligatorio(a.get(1), ProtocoloPruebas.CAMPO_ID_PRUEBA)))));
    }

    /** Número de campos de incidencias que debe haber, según el campo {@code n} de la petición. */
    private static int incidenciasEsperadas(List<String> a) {
        try {
            int n = Integer.parseInt(a.get(4).strip());
            if (n < 0) {
                throw new IllegalArgumentException("Número de incidencias negativo");
            }
            return n * ProtocoloPruebas.CAMPOS_INCIDENCIA_NUEVA;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Número de incidencias no válido", e);
        }
    }

    private static List<NuevaIncidencia> incidencias(List<String> a, int desde) {
        List<NuevaIncidencia> lista = new ArrayList<>();
        for (int p = desde; p < a.size(); p += ProtocoloPruebas.CAMPOS_INCIDENCIA_NUEVA) {
            lista.add(incidencia(a.get(p), a.get(p + 1)));
        }
        return lista;
    }

    private static NuevaIncidencia incidencia(String tipo, String descripcion) {
        return new NuevaIncidencia(convertir(tipo, ProtocoloPruebas.CAMPO_TIPO, TipoIncidencia::valueOf), descripcion);
    }

    /**
     * Comprueba el número de argumentos, ejecuta la operación y convierte una
     * {@link PruebaException} en {@code ERROR;<código>;<detalle>}.
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
        } catch (PruebaException e) {
            return Protocolo.respuestaErrorConDetalle(e.codigo(), e.detalle());
        }
    }

    // ---- Conversión de campos ----

    private static long idObligatorio(String texto, String campo) {
        Long id = convertir(texto, campo, Long::valueOf);
        if (id == null) {
            throw new PruebaException(CodigoError.CAMPO_OBLIGATORIO, campo);
        }
        return id;
    }

    private static LocalDateTime fechaHora(String texto) {
        return convertir(texto, ProtocoloPruebas.CAMPO_FECHA_HORA, LocalDateTime::parse);
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
            throw new PruebaException(CodigoError.CAMPO_NO_VALIDO, campo);
        }
    }
}
