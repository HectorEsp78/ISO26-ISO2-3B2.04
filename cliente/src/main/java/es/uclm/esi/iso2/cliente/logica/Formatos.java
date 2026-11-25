package es.uclm.esi.iso2.cliente.logica;

import es.uclm.esi.iso2.comun.dominio.Paciente;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;

/**
 * Conversión entre lo que ve el usuario en pantalla y lo que viaja por el protocolo.
 */
public final class Formatos {

    /** Formato de fecha que ve el usuario. */
    public static final DateTimeFormatter FECHA_PANTALLA =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    public static final String SEXO_SIN_INDICAR = "Sin indicar";
    public static final String SEXO_HOMBRE = "Hombre";
    public static final String SEXO_MUJER = "Mujer";
    public static final List<String> OPCIONES_SEXO = List.of(SEXO_SIN_INDICAR, SEXO_HOMBRE, SEXO_MUJER);

    private Formatos() {
    }

    /**
     * Pasa una fecha escrita por el usuario ({@code dd/mm/aaaa}) a formato ISO. Si el texto está
     * vacío devuelve la cadena vacía, y si no se puede interpretar lo devuelve tal cual para que
     * la validación lo marque como no válido.
     */
    public static String fechaAIso(String textoUsuario) {
        String texto = textoUsuario == null ? "" : textoUsuario.strip();
        if (texto.isEmpty()) {
            return "";
        }
        try {
            return LocalDate.parse(texto, FECHA_PANTALLA).toString();
        } catch (DateTimeParseException e) {
            return texto;
        }
    }

    /** Pasa una fecha ISO a {@code dd/mm/aaaa}; si no es una fecha ISO la devuelve tal cual. */
    public static String fechaAPantalla(String iso) {
        if (iso == null || iso.isBlank()) {
            return "";
        }
        try {
            return LocalDate.parse(iso).format(FECHA_PANTALLA);
        } catch (DateTimeParseException e) {
            return iso;
        }
    }

    /** Opción del desplegable de sexo a valor del protocolo. */
    public static String sexoAProtocolo(String opcion) {
        if (SEXO_HOMBRE.equals(opcion)) {
            return Paciente.HOMBRE;
        }
        if (SEXO_MUJER.equals(opcion)) {
            return Paciente.MUJER;
        }
        return "";
    }

    /** Valor del protocolo a opción del desplegable de sexo. */
    public static String sexoAPantalla(String valor) {
        if (Paciente.HOMBRE.equals(valor)) {
            return SEXO_HOMBRE;
        }
        if (Paciente.MUJER.equals(valor)) {
            return SEXO_MUJER;
        }
        return SEXO_SIN_INDICAR;
    }

    /** Texto que se muestra para un valor opcional vacío. */
    public static String oGuion(String valor) {
        return valor == null || valor.isBlank() ? "—" : valor;
    }
}
