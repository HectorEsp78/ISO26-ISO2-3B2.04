package es.uclm.esi.iso2.cliente.servicio;

import es.uclm.esi.iso2.comun.protocolo.Protocolo;

import java.util.function.UnaryOperator;

/**
 * Crea el servicio de pacientes según la configuración (DEC-059).
 *
 * <ul>
 *   <li>{@code radiodiagnostico.modo}: {@code socket} (por defecto) o {@code memoria}.</li>
 *   <li>{@code radiodiagnostico.host}: por defecto {@code localhost}.</li>
 *   <li>{@code radiodiagnostico.puerto}: por defecto {@value Protocolo#PUERTO_POR_DEFECTO}.</li>
 * </ul>
 */
public final class FabricaServicio {

    public static final String PROPIEDAD_MODO = "radiodiagnostico.modo";
    public static final String PROPIEDAD_HOST = "radiodiagnostico.host";
    public static final String PROPIEDAD_PUERTO = "radiodiagnostico.puerto";
    public static final String MODO_SOCKET = "socket";
    public static final String MODO_MEMORIA = "memoria";

    private FabricaServicio() {
    }

    /**
     * Crea el servicio leyendo la configuración con la función indicada (normalmente
     * {@code System::getProperty}).
     *
     * @throws IllegalArgumentException si el modo o el puerto no son válidos
     */
    public static ServicioPacientes crear(UnaryOperator<String> propiedades) {
        String modo = valor(propiedades, PROPIEDAD_MODO, MODO_SOCKET);
        if (MODO_MEMORIA.equals(modo)) {
            return ServicioPacientesEnMemoria.conDatosDeEjemplo();
        }
        if (!MODO_SOCKET.equals(modo)) {
            throw new IllegalArgumentException("Modo desconocido: " + modo);
        }
        String host = valor(propiedades, PROPIEDAD_HOST, "localhost");
        String textoPuerto = valor(propiedades, PROPIEDAD_PUERTO, String.valueOf(Protocolo.PUERTO_POR_DEFECTO));
        int puerto;
        try {
            puerto = Integer.parseInt(textoPuerto);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Puerto no válido: " + textoPuerto, e);
        }
        if (puerto < 1 || puerto > 65535) {
            throw new IllegalArgumentException("Puerto no válido: " + textoPuerto);
        }
        return new ServicioPacientesSocket(host, puerto);
    }

    private static String valor(UnaryOperator<String> propiedades, String clave, String porDefecto) {
        String valor = propiedades.apply(clave);
        return valor == null || valor.isBlank() ? porDefecto : valor.strip();
    }
}
