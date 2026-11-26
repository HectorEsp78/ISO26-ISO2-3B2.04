package es.uclm.esi.iso2.servidor.red;

import es.uclm.esi.iso2.comun.protocolo.CodigoError;
import es.uclm.esi.iso2.comun.protocolo.Protocolo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Traduce cada línea recibida en la respuesta que se envía al cliente (DEC-039).
 *
 * <p>Cada caso de uso registra sus operaciones con {@link #registrar}: CU12 lo hace en
 * {@link OperacionesPacientes} y CU13 lo hará con las suyas (DEC-068). Si una operación lanza
 * una excepción no controlada, se responde {@code ERROR_INTERNO} sin cortar la conexión.</p>
 */
public class ManejadorPeticiones {

    private static final System.Logger LOG = System.getLogger(ManejadorPeticiones.class.getName());

    private final Map<String, Function<List<String>, String>> operaciones = new ConcurrentHashMap<>();

    public ManejadorPeticiones() {
        registrar(Protocolo.PING, argumentos -> Protocolo.componer(Protocolo.OK, "PONG"));
    }

    /**
     * Registra una operación.
     *
     * @param nombre    nombre de la operación (primer campo de la petición)
     * @param operacion recibe los argumentos (sin el nombre) y devuelve la línea de respuesta;
     *                  si los argumentos no son válidos lanza {@link IllegalArgumentException}
     * @throws IllegalStateException si ya hay una operación con ese nombre
     */
    public void registrar(String nombre, Function<List<String>, String> operacion) {
        if (operaciones.putIfAbsent(nombre, operacion) != null) {
            throw new IllegalStateException("Operación ya registrada: " + nombre);
        }
    }

    /** Atiende una petición y devuelve la respuesta (nunca {@code null}). */
    public String atender(String linea) {
        List<String> campos;
        try {
            campos = Protocolo.descomponer(linea);
        } catch (IllegalArgumentException e) {
            return Protocolo.respuestaError(CodigoError.PETICION_NO_VALIDA, null);
        }
        Function<List<String>, String> operacion = operaciones.get(campos.get(0));
        if (operacion == null) {
            return Protocolo.respuestaError(CodigoError.PETICION_NO_VALIDA, null);
        }
        try {
            return operacion.apply(campos.subList(1, campos.size()));
        } catch (IllegalArgumentException e) {
            return Protocolo.respuestaError(CodigoError.PETICION_NO_VALIDA, null);
        } catch (RuntimeException e) {
            LOG.log(System.Logger.Level.ERROR, "Error al atender " + campos.get(0), e);
            return Protocolo.respuestaError(CodigoError.ERROR_INTERNO, null);
        }
    }
}
