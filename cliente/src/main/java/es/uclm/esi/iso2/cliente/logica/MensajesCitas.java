package es.uclm.esi.iso2.cliente.logica;

import es.uclm.esi.iso2.comun.dominio.CampoPaciente;
import es.uclm.esi.iso2.comun.protocolo.CodigoError;
import es.uclm.esi.iso2.comun.protocolo.ProtocoloCitas;

/**
 * Traduce los errores de citas (DEC-065) a mensajes para el personal administrativo.
 *
 * <p>Los errores propios de las citas, o que necesitan un texto distinto en este caso de uso, se
 * resuelven aquí. El resto (sin conexión, error interno...) se delegan en {@link MensajesError},
 * para no repetirlos ni tocar ese archivo.</p>
 */
public final class MensajesCitas {

    private MensajesCitas() {
    }

    /** Nombre del campo tal y como aparece en pantalla. */
    public static String etiqueta(String campo) {
        return switch (campo) {
            case ProtocoloCitas.CAMPO_ID -> "Número de cita";
            case ProtocoloCitas.CAMPO_DNI -> "DNI/NIE del paciente";
            case ProtocoloCitas.CAMPO_TIPO_PRUEBA -> "Tipo de prueba";
            case ProtocoloCitas.CAMPO_RECURSO -> "Recurso";
            case ProtocoloCitas.CAMPO_INICIO -> "Fecha y hora";
            case GestorCitasCliente.CAMPO_FECHA -> "Fecha";
            case GestorCitasCliente.CAMPO_HORA -> "Hora";
            case ProtocoloCitas.CAMPO_DURACION -> "Duración";
            case ProtocoloCitas.CAMPO_MOTIVO -> "Motivo de la cancelación";
            case ProtocoloCitas.CAMPO_DESDE -> "Desde";
            case ProtocoloCitas.CAMPO_HASTA -> "Hasta";
            case ProtocoloCitas.CAMPO_ESTADO -> "Estado";
            default -> campo;
        };
    }

    /** Explicación de lo que se espera en cada campo. */
    public static String formatoEsperado(String campo) {
        return switch (campo) {
            case ProtocoloCitas.CAMPO_DNI -> MensajesError.formatoEsperado(CampoPaciente.DNI);
            case ProtocoloCitas.CAMPO_TIPO_PRUEBA, ProtocoloCitas.CAMPO_RECURSO -> "uno de los de la lista";
            case GestorCitasCliente.CAMPO_FECHA, ProtocoloCitas.CAMPO_DESDE -> "una fecha dd/mm/aaaa";
            case GestorCitasCliente.CAMPO_HORA -> "una hora hh:mm, por ejemplo 09:30";
            case ProtocoloCitas.CAMPO_HASTA -> "una fecha dd/mm/aaaa que no sea anterior a «Desde»";
            case ProtocoloCitas.CAMPO_DURACION -> "un número de minutos mayor que 0";
            case ProtocoloCitas.CAMPO_MOTIVO -> "un texto de hasta 255 caracteres";
            default -> "un valor válido";
        };
    }

    /**
     * Mensaje para un error.
     *
     * @param detalle nombre del campo que falla o, en un solape, número de la cita con la que choca
     *                (FA-04 y FA-05); puede ser {@code null}
     */
    public static String mensaje(CodigoError codigo, String detalle) {
        return switch (codigo) {
            case CAMPO_OBLIGATORIO -> detalle == null
                    ? "Falta un campo obligatorio."
                    : "Falta el campo obligatorio «" + etiqueta(detalle) + "».";
            case CAMPO_NO_VALIDO -> detalle == null
                    ? "Hay un campo con un formato no válido."
                    : "El campo «" + etiqueta(detalle) + "» no es válido: debe ser " + formatoEsperado(detalle) + ".";
            case PACIENTE_NO_ENCONTRADO -> "No existe ningún paciente con ese DNI/NIE. Hay que darlo de alta antes de"
                    + " asignarle una cita.";
            case PACIENTE_DE_BAJA -> "El paciente está dado de baja: no se le pueden asignar citas.";
            case SOLAPE_RECURSO, SOLAPE_PACIENTE -> conCitaQueChoca(MensajesError.mensaje(codigo, null), detalle);
            default -> MensajesError.mensaje(codigo, null);
        };
    }

    /** Añade «(choca con la cita nº X)» al mensaje de un solape, si se sabe con qué cita choca. */
    private static String conCitaQueChoca(String mensaje, String idCita) {
        if (idCita == null || idCita.isBlank()) {
            return mensaje;
        }
        return mensaje.substring(0, mensaje.length() - 1) + " (choca con la cita nº " + idCita + ").";
    }
}
