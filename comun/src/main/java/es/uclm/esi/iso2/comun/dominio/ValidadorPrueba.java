package es.uclm.esi.iso2.comun.dominio;

import es.uclm.esi.iso2.comun.protocolo.CodigoError;
import es.uclm.esi.iso2.comun.protocolo.ProtocoloPruebas;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Validaciones de los datos de una prueba y de sus incidencias (CU10, apartado 8, y DEC-074).
 *
 * <p>La usan el cliente y el servidor, de modo que los dos aplican las mismas reglas. El
 * servidor valida siempre, aunque el cliente ya lo haya hecho. Cada método devuelve el primer
 * error que encuentra, en el orden en que el médico rellena los campos.</p>
 */
public final class ValidadorPrueba {

    public static final int MAX_ID_MEDICO = 20;
    public static final int MAX_OBSERVACIONES = 1000;
    public static final int MAX_MOTIVO = 255;
    public static final int MAX_DESCRIPCION = 500;

    private ValidadorPrueba() {
    }

    /** Quita los espacios del principio y del final; {@code null} se convierte en cadena vacía. */
    public static String normalizar(String texto) {
        return texto == null ? "" : texto.strip();
    }

    /** Identificador del médico: de 1 a 20 caracteres (DEC-072). */
    public static Optional<ErrorCampo> validarMedico(String idMedico) {
        String limpio = normalizar(idMedico);
        if (limpio.isEmpty()) {
            return error(ProtocoloPruebas.CAMPO_MEDICO, CodigoError.CAMPO_OBLIGATORIO);
        }
        if (limpio.length() > MAX_ID_MEDICO) {
            return error(ProtocoloPruebas.CAMPO_MEDICO, CodigoError.CAMPO_NO_VALIDO);
        }
        return Optional.empty();
    }

    /**
     * Datos de una prueba realizada: fecha y hora obligatoria y no futura (CA-09), observaciones
     * de hasta 1000 caracteres e incidencias válidas.
     *
     * @param ahora momento actual, para comprobar que la fecha no es futura
     */
    public static Optional<ErrorCampo> validarRealizacion(LocalDateTime fechaHora, String observaciones,
            List<NuevaIncidencia> incidencias, LocalDateTime ahora) {
        if (fechaHora == null) {
            return error(ProtocoloPruebas.CAMPO_FECHA_HORA, CodigoError.CAMPO_OBLIGATORIO);
        }
        if (fechaHora.isAfter(ahora)) {
            return error(ProtocoloPruebas.CAMPO_FECHA_HORA, CodigoError.FECHA_FUTURA);
        }
        if (normalizar(observaciones).length() > MAX_OBSERVACIONES) {
            return error(ProtocoloPruebas.CAMPO_OBSERVACIONES, CodigoError.CAMPO_NO_VALIDO);
        }
        if (incidencias != null) {
            for (NuevaIncidencia incidencia : incidencias) {
                Optional<ErrorCampo> error = validarIncidencia(incidencia);
                if (error.isPresent()) {
                    return error;
                }
            }
        }
        return Optional.empty();
    }

    /** Motivo de cancelación: obligatorio, de 1 a 255 caracteres (DEC-046). */
    public static Optional<ErrorCampo> validarMotivo(String motivo) {
        String limpio = normalizar(motivo);
        if (limpio.isEmpty()) {
            return error(ProtocoloPruebas.CAMPO_MOTIVO, CodigoError.CAMPO_OBLIGATORIO);
        }
        if (limpio.length() > MAX_MOTIVO) {
            return error(ProtocoloPruebas.CAMPO_MOTIVO, CodigoError.CAMPO_NO_VALIDO);
        }
        return Optional.empty();
    }

    /** Incidencia: tipo obligatorio y descripción de 1 a 500 caracteres. */
    public static Optional<ErrorCampo> validarIncidencia(NuevaIncidencia incidencia) {
        if (incidencia == null || incidencia.tipo() == null) {
            return error(ProtocoloPruebas.CAMPO_TIPO, CodigoError.CAMPO_OBLIGATORIO);
        }
        String descripcion = normalizar(incidencia.descripcion());
        if (descripcion.isEmpty()) {
            return error(ProtocoloPruebas.CAMPO_DESCRIPCION, CodigoError.CAMPO_OBLIGATORIO);
        }
        if (descripcion.length() > MAX_DESCRIPCION) {
            return error(ProtocoloPruebas.CAMPO_DESCRIPCION, CodigoError.CAMPO_NO_VALIDO);
        }
        return Optional.empty();
    }

    /** Filtros de búsqueda: la fecha «hasta» no puede ser anterior a «desde». */
    public static Optional<ErrorCampo> validarFiltro(FiltroPruebas filtro) {
        if (filtro.desde() != null && filtro.hasta() != null && filtro.hasta().isBefore(filtro.desde())) {
            return error(ProtocoloPruebas.CAMPO_HASTA, CodigoError.CAMPO_NO_VALIDO);
        }
        return Optional.empty();
    }

    private static Optional<ErrorCampo> error(String campo, CodigoError codigo) {
        return Optional.of(new ErrorCampo(campo, codigo));
    }
}
