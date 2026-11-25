package es.uclm.esi.iso2.comun.protocolo;

import es.uclm.esi.iso2.comun.dominio.CampoPaciente;
import es.uclm.esi.iso2.comun.dominio.EstadoPaciente;
import es.uclm.esi.iso2.comun.dominio.Paciente;

import java.util.ArrayList;
import java.util.List;

/**
 * Protocolo de texto entre cliente y servidor (DEC-039 y DEC-057).
 *
 * <p>Cada mensaje es una línea UTF-8 con campos separados por {@code ;}. Dentro de un campo,
 * {@code \} se escribe {@code \\}, {@code ;} se escribe {@code \;}, el salto de línea
 * {@code \n} y el retorno de carro {@code \r}.</p>
 */
public final class Protocolo {

    public static final int PUERTO_POR_DEFECTO = 5050;

    public static final String PING = "PING";
    public static final String ALTA_PACIENTE = "ALTA_PACIENTE";
    public static final String BUSCAR_PACIENTE = "BUSCAR_PACIENTE";
    public static final String MODIFICAR_PACIENTE = "MODIFICAR_PACIENTE";
    public static final String BAJA_PACIENTE = "BAJA_PACIENTE";

    public static final String OK = "OK";
    public static final String ERROR = "ERROR";

    /** Número de campos de los datos de un paciente (sin el estado). */
    public static final int CAMPOS_DATOS = CampoPaciente.values().length;
    /** Número de campos de un paciente completo (datos y estado). */
    public static final int CAMPOS_PACIENTE = CAMPOS_DATOS + 1;

    private static final char SEPARADOR = ';';
    private static final char ESCAPE = '\\';

    private Protocolo() {
    }

    // ---- Codificación de campos ----

    /** Escapa un campo para que se pueda enviar dentro de un mensaje. */
    public static String escapar(String campo) {
        StringBuilder sb = new StringBuilder(campo.length());
        for (char c : campo.toCharArray()) {
            switch (c) {
                case ESCAPE -> sb.append("\\\\");
                case SEPARADOR -> sb.append("\\;");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                default -> sb.append(c);
            }
        }
        return sb.toString();
    }

    /** Une los campos en un mensaje, escapando cada uno. */
    public static String componer(List<String> campos) {
        List<String> escapados = new ArrayList<>(campos.size());
        for (String campo : campos) {
            escapados.add(escapar(campo == null ? "" : campo));
        }
        return String.join(String.valueOf(SEPARADOR), escapados);
    }

    /** Une los campos en un mensaje, escapando cada uno. */
    public static String componer(String... campos) {
        return componer(List.of(campos));
    }

    /**
     * Separa un mensaje en sus campos y deshace el escape.
     *
     * @throws IllegalArgumentException si el mensaje tiene una secuencia de escape no válida
     */
    public static List<String> descomponer(String linea) {
        List<String> campos = new ArrayList<>();
        StringBuilder actual = new StringBuilder();
        for (int i = 0; i < linea.length(); i++) {
            char c = linea.charAt(i);
            if (c == SEPARADOR) {
                campos.add(actual.toString());
                actual.setLength(0);
            } else if (c == ESCAPE) {
                if (i + 1 >= linea.length()) {
                    throw new IllegalArgumentException("Escape incompleto al final del mensaje");
                }
                char siguiente = linea.charAt(++i);
                actual.append(switch (siguiente) {
                    case ESCAPE -> ESCAPE;
                    case SEPARADOR -> SEPARADOR;
                    case 'n' -> '\n';
                    case 'r' -> '\r';
                    default -> throw new IllegalArgumentException("Secuencia de escape no válida: \\" + siguiente);
                });
            } else {
                actual.append(c);
            }
        }
        campos.add(actual.toString());
        return campos;
    }

    // ---- Pacientes ----

    /** Los 9 campos de datos del paciente, en el orden de {@link CampoPaciente}. */
    public static List<String> camposDatos(Paciente paciente) {
        List<String> campos = new ArrayList<>(CAMPOS_PACIENTE);
        for (CampoPaciente campo : CampoPaciente.values()) {
            campos.add(paciente.valor(campo));
        }
        return campos;
    }

    /** Los 9 campos de datos seguidos del estado. */
    public static List<String> camposPaciente(Paciente paciente) {
        List<String> campos = camposDatos(paciente);
        campos.add(paciente.estado().name());
        return campos;
    }

    /**
     * Construye un paciente {@link EstadoPaciente#ACTIVO} a partir de los 9 campos de datos que
     * empiezan en la posición indicada.
     *
     * @throws IllegalArgumentException si no hay exactamente 9 campos desde esa posición
     */
    public static Paciente pacienteDesdeDatos(List<String> campos, int desde) {
        comprobarNumeroCampos(campos, desde, CAMPOS_DATOS);
        return crear(campos, desde, EstadoPaciente.ACTIVO);
    }

    /**
     * Construye un paciente a partir de los 10 campos (datos y estado) que empiezan en la
     * posición indicada.
     *
     * @throws IllegalArgumentException si el número de campos o el estado no son válidos
     */
    public static Paciente pacienteDesdeCampos(List<String> campos, int desde) {
        comprobarNumeroCampos(campos, desde, CAMPOS_PACIENTE);
        return crear(campos, desde, EstadoPaciente.valueOf(campos.get(desde + CAMPOS_DATOS)));
    }

    // ---- Peticiones ----

    public static String peticionAlta(Paciente paciente) {
        return peticionConDatos(ALTA_PACIENTE, paciente);
    }

    public static String peticionBuscar(String dni) {
        return componer(BUSCAR_PACIENTE, dni);
    }

    public static String peticionModificar(Paciente paciente) {
        return peticionConDatos(MODIFICAR_PACIENTE, paciente);
    }

    public static String peticionBaja(String dni) {
        return componer(BAJA_PACIENTE, dni);
    }

    // ---- Respuestas ----

    /** Respuesta correcta con un paciente: {@code OK;<paciente>}. */
    public static String respuestaOk(Paciente paciente) {
        List<String> campos = new ArrayList<>();
        campos.add(OK);
        campos.addAll(camposPaciente(paciente));
        return componer(campos);
    }

    /** Respuesta de error: {@code ERROR;<código>} o {@code ERROR;<código>;<campo>}. */
    public static String respuestaError(CodigoError codigo, CampoPaciente campo) {
        return campo == null
                ? componer(ERROR, codigo.name())
                : componer(ERROR, codigo.name(), campo.nombreProtocolo());
    }

    private static String peticionConDatos(String operacion, Paciente paciente) {
        List<String> campos = new ArrayList<>();
        campos.add(operacion);
        campos.addAll(camposDatos(paciente));
        return componer(campos);
    }

    private static Paciente crear(List<String> c, int i, EstadoPaciente estado) {
        return new Paciente(c.get(i), c.get(i + 1), c.get(i + 2), c.get(i + 3), c.get(i + 4),
                c.get(i + 5), c.get(i + 6), c.get(i + 7), c.get(i + 8), estado);
    }

    private static void comprobarNumeroCampos(List<String> campos, int desde, int esperados) {
        if (campos.size() - desde != esperados) {
            throw new IllegalArgumentException(
                    "Se esperaban " + esperados + " campos y hay " + (campos.size() - desde));
        }
    }
}
