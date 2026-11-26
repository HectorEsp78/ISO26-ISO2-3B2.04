package es.uclm.esi.iso2.cliente.logica;

import es.uclm.esi.iso2.comun.protocolo.CodigoError;
import es.uclm.esi.iso2.comun.protocolo.ProtocoloPruebas;

/**
 * Mensajes de los errores de campo de CU10: dicen cuál es el campo y qué se espera de él.
 */
public final class MensajesPruebas {

    private MensajesPruebas() {
    }

    /** Nombre del campo tal y como aparece en las pantallas. */
    public static String etiqueta(String campo) {
        return switch (campo == null ? "" : campo) {
            case ProtocoloPruebas.CAMPO_MEDICO -> "Médico";
            case ProtocoloPruebas.CAMPO_DNI -> "DNI/NIE";
            case ProtocoloPruebas.CAMPO_ID_SOLICITUD -> "Solicitud";
            case ProtocoloPruebas.CAMPO_ID_PRUEBA -> "Prueba";
            case ProtocoloPruebas.CAMPO_FECHA_HORA -> "Fecha y hora de realización";
            case ProtocoloPruebas.CAMPO_OBSERVACIONES -> "Observaciones";
            case ProtocoloPruebas.CAMPO_MOTIVO -> "Motivo de la cancelación";
            case ProtocoloPruebas.CAMPO_TIPO -> "Tipo de incidencia";
            case ProtocoloPruebas.CAMPO_DESCRIPCION -> "Descripción de la incidencia";
            case ProtocoloPruebas.CAMPO_DESDE -> "Desde";
            case ProtocoloPruebas.CAMPO_HASTA -> "Hasta";
            case ProtocoloPruebas.CAMPO_ESTADO -> "Estado";
            default -> "";
        };
    }

    /** Qué se espera en cada campo. */
    public static String formatoEsperado(String campo) {
        return switch (campo == null ? "" : campo) {
            case ProtocoloPruebas.CAMPO_FECHA_HORA -> "una fecha y hora dd/mm/aaaa hh:mm";
            case ProtocoloPruebas.CAMPO_OBSERVACIONES -> "hasta 1000 caracteres";
            case ProtocoloPruebas.CAMPO_MOTIVO -> "de 1 a 255 caracteres";
            case ProtocoloPruebas.CAMPO_DESCRIPCION -> "de 1 a 500 caracteres";
            case ProtocoloPruebas.CAMPO_DESDE, ProtocoloPruebas.CAMPO_HASTA -> "una fecha dd/mm/aaaa, y «Hasta» no puede ser anterior a «Desde»";
            case ProtocoloPruebas.CAMPO_ID_SOLICITUD -> "un número";
            case ProtocoloPruebas.CAMPO_MEDICO -> "de 1 a 20 caracteres";
            default -> "un valor válido";
        };
    }

    /** Mensaje de un error de validación de un campo. */
    public static String mensajeDeCampo(CodigoError codigo, String campo) {
        String etiqueta = etiqueta(campo);
        return switch (codigo) {
            case CAMPO_OBLIGATORIO -> etiqueta.isEmpty()
                    ? "Falta un campo obligatorio."
                    : "Falta el campo obligatorio «" + etiqueta + "».";
            case CAMPO_NO_VALIDO -> etiqueta.isEmpty()
                    ? "Hay un campo con un formato no válido."
                    : "El campo «" + etiqueta + "» no es válido: debe ser " + formatoEsperado(campo) + ".";
            default -> MensajesError.mensaje(codigo, null);
        };
    }
}
