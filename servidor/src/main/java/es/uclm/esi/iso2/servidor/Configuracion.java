package es.uclm.esi.iso2.servidor;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Locale;
import java.util.Properties;
import java.util.function.UnaryOperator;

/**
 * Configuración del servidor (DEC-069).
 *
 * <p>Cada valor se busca, por este orden, en las propiedades del sistema ({@code -Dclave=valor}),
 * en las variables de entorno (la clave en mayúsculas y con {@code _}, por ejemplo
 * {@code RADIODIAGNOSTICO_BD_URL}) y en el fichero {@code servidor.properties}.</p>
 */
public final class Configuracion {

    public static final String PUERTO = "radiodiagnostico.puerto";
    public static final String BD_URL = "radiodiagnostico.bd.url";
    public static final String BD_USUARIO = "radiodiagnostico.bd.usuario";
    public static final String BD_CONTRASENA = "radiodiagnostico.bd.contrasena";

    private final UnaryOperator<String> propiedadesSistema;
    private final UnaryOperator<String> entorno;
    private final Properties fichero;

    public Configuracion(UnaryOperator<String> propiedadesSistema, UnaryOperator<String> entorno, Properties fichero) {
        this.propiedadesSistema = propiedadesSistema;
        this.entorno = entorno;
        this.fichero = fichero;
    }

    /** Configuración real: propiedades del sistema, entorno y {@code servidor.properties}. */
    public static Configuracion delSistema() {
        return new Configuracion(System::getProperty, System::getenv, cargar("/servidor.properties"));
    }

    /** Lee un fichero de propiedades del classpath. */
    static Properties cargar(String recurso) {
        Properties propiedades = new Properties();
        try (InputStream entrada = Configuracion.class.getResourceAsStream(recurso)) {
            if (entrada == null) {
                throw new IllegalStateException("No se encuentra " + recurso);
            }
            propiedades.load(entrada);
            return propiedades;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Valor de la clave indicada.
     *
     * @throws IllegalStateException si no está definida en ningún sitio
     */
    public String valor(String clave) {
        String valor = propiedadesSistema.apply(clave);
        if (vacio(valor)) {
            valor = entorno.apply(clave.toUpperCase(Locale.ROOT).replace('.', '_'));
        }
        if (vacio(valor)) {
            valor = fichero.getProperty(clave);
        }
        if (vacio(valor)) {
            throw new IllegalStateException("Falta la configuración " + clave);
        }
        return valor.strip();
    }

    /**
     * Puerto en el que escucha el servidor.
     *
     * @throws IllegalStateException si no es un número entre 1 y 65535
     */
    public int puerto() {
        String texto = valor(PUERTO);
        try {
            int puerto = Integer.parseInt(texto);
            if (puerto >= 1 && puerto <= 65535) {
                return puerto;
            }
        } catch (NumberFormatException e) {
            // se informa abajo
        }
        throw new IllegalStateException("Puerto no válido: " + texto);
    }

    private static boolean vacio(String valor) {
        return valor == null || valor.isBlank();
    }
}
