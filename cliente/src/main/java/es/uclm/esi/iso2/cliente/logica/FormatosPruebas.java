package es.uclm.esi.iso2.cliente.logica;

import es.uclm.esi.iso2.comun.dominio.EstadoPrueba;
import es.uclm.esi.iso2.comun.dominio.TipoIncidencia;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Optional;

/**
 * Conversión entre lo que ve el usuario en las pantallas de pruebas y los valores del protocolo.
 */
public final class FormatosPruebas {

    public static final DateTimeFormatter FECHA_HORA_PANTALLA =
            DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm").withResolverStyle(ResolverStyle.STRICT);

    private FormatosPruebas() {
    }

    /** Fecha y hora escritas como {@code dd/mm/aaaa hh:mm}; vacío si el texto está vacío o no es válido. */
    public static Optional<LocalDateTime> fechaHora(String texto) {
        if (texto == null || texto.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.of(LocalDateTime.parse(texto.strip(), FECHA_HORA_PANTALLA));
        } catch (DateTimeParseException e) {
            return Optional.empty();
        }
    }

    /** Fecha escrita como {@code dd/mm/aaaa}; vacío si el texto está vacío o no es válido. */
    public static Optional<LocalDate> fecha(String texto) {
        if (texto == null || texto.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.of(LocalDate.parse(texto.strip(), Formatos.FECHA_PANTALLA));
        } catch (DateTimeParseException e) {
            return Optional.empty();
        }
    }

    public static String aPantalla(LocalDateTime fechaHora) {
        return fechaHora == null ? "—" : fechaHora.format(FECHA_HORA_PANTALLA);
    }

    public static String aPantalla(EstadoPrueba estado) {
        return switch (estado) {
            case REALIZADA -> "Realizada";
            case CANCELADA -> "Cancelada";
        };
    }

    public static String aPantalla(TipoIncidencia tipo) {
        return switch (tipo) {
            case TECNICA -> "Técnica";
            case PACIENTE -> "Del paciente";
            case MATERIAL -> "De material";
            case OTRA -> "Otra";
        };
    }
}
