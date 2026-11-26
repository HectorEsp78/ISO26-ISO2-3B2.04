package es.uclm.esi.iso2.cliente.servicio;

import es.uclm.esi.iso2.comun.protocolo.Protocolo;

import java.util.function.UnaryOperator;

/**
 * Crea el servicio de pruebas según la configuración (DEC-059 y DEC-080). Usa las mismas
 * propiedades que {@link FabricaServicio}: {@code radiodiagnostico.modo}, {@code .host} y
 * {@code .puerto}, más {@code radiodiagnostico.medico} (identificador del médico, por defecto
 * {@value #MEDICO_POR_DEFECTO}) hasta que exista un inicio de sesión.
 */
public final class FabricaServicioPruebas {

    public static final String PROPIEDAD_MEDICO = "radiodiagnostico.medico";
    public static final String MEDICO_POR_DEFECTO = "medico1";

    private FabricaServicioPruebas() {
    }

    /**
     * @throws IllegalArgumentException si el modo o el puerto no son válidos
     */
    public static ServicioPruebas crear(UnaryOperator<String> propiedades) {
        String modo = valor(propiedades, FabricaServicio.PROPIEDAD_MODO, FabricaServicio.MODO_SOCKET);
        if (FabricaServicio.MODO_MEMORIA.equals(modo)) {
            return ServicioPruebasEnMemoria.conDatosDeEjemplo(idMedico(propiedades));
        }
        if (!FabricaServicio.MODO_SOCKET.equals(modo)) {
            throw new IllegalArgumentException("Modo desconocido: " + modo);
        }
        String host = valor(propiedades, FabricaServicio.PROPIEDAD_HOST, "localhost");
        String textoPuerto = valor(propiedades, FabricaServicio.PROPIEDAD_PUERTO,
                String.valueOf(Protocolo.PUERTO_POR_DEFECTO));
        int puerto;
        try {
            puerto = Integer.parseInt(textoPuerto);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Puerto no válido: " + textoPuerto, e);
        }
        if (puerto < 1 || puerto > 65535) {
            throw new IllegalArgumentException("Puerto no válido: " + textoPuerto);
        }
        return new ServicioPruebasSocket(host, puerto);
    }

    /** Identificador del médico que usa este cliente. */
    public static String idMedico(UnaryOperator<String> propiedades) {
        return valor(propiedades, PROPIEDAD_MEDICO, MEDICO_POR_DEFECTO);
    }

    private static String valor(UnaryOperator<String> propiedades, String clave, String porDefecto) {
        String valor = propiedades.apply(clave);
        return valor == null || valor.isBlank() ? porDefecto : valor.strip();
    }
}
