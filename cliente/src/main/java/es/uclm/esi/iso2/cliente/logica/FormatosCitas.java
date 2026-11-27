package es.uclm.esi.iso2.cliente.logica;

import es.uclm.esi.iso2.comun.dominio.EstadoCita;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

/**
 * Cómo se muestran en pantalla las fechas, horas y estados de las citas (CU13). Las fechas se
 * escriben igual que en CU12 ({@link Formatos#FECHA_PANTALLA}).
 */
public final class FormatosCitas {

    /** Formato de hora que escribe y ve el usuario. */
    public static final DateTimeFormatter HORA_PANTALLA =
            DateTimeFormatter.ofPattern("HH:mm").withResolverStyle(ResolverStyle.STRICT);

    private FormatosCitas() {
    }

    /** {@code dd/mm/aaaa hh:mm}, o cadena vacía si es {@code null}. */
    public static String fechaHora(LocalDateTime fechaHora) {
        return fechaHora == null ? "" : fecha(fechaHora.toLocalDate()) + " " + hora(fechaHora.toLocalTime());
    }

    /** {@code dd/mm/aaaa}, o cadena vacía si es {@code null}. */
    public static String fecha(LocalDate fecha) {
        return fecha == null ? "" : fecha.format(Formatos.FECHA_PANTALLA);
    }

    /** {@code hh:mm}, o cadena vacía si es {@code null}. */
    public static String hora(LocalTime hora) {
        return hora == null ? "" : hora.format(HORA_PANTALLA);
    }

    /** Nombre del estado para el usuario. */
    public static String estado(EstadoCita estado) {
        return switch (estado) {
            case PROGRAMADA -> "Programada";
            case CANCELADA -> "Cancelada";
            case REALIZADA -> "Realizada";
        };
    }
}
