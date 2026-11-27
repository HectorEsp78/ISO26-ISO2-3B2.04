package es.uclm.esi.iso2.cliente.logica;

import es.uclm.esi.iso2.comun.dominio.CampoPaciente;
import es.uclm.esi.iso2.comun.protocolo.CodigoError;

/**
 * Traduce los errores del protocolo (DEC-057) a mensajes para el personal administrativo.
 */
public final class MensajesError {

    private MensajesError() {
    }

    /** Nombre del campo tal y como aparece en el formulario. */
    public static String etiqueta(CampoPaciente campo) {
        return switch (campo) {
            case DNI -> "DNI/NIE";
            case TARJETA_SANITARIA -> "Tarjeta sanitaria";
            case NOMBRE -> "Nombre";
            case APELLIDOS -> "Apellidos";
            case FECHA_NACIMIENTO -> "Fecha de nacimiento";
            case SEXO -> "Sexo";
            case TELEFONO -> "Teléfono";
            case CORREO -> "Correo electrónico";
            case DIRECCION -> "Dirección";
        };
    }

    /** Explicación del formato que se espera en cada campo (DEC-055). */
    public static String formatoEsperado(CampoPaciente campo) {
        return switch (campo) {
            case DNI -> "8 dígitos y letra (DNI) o X/Y/Z, 7 dígitos y letra (NIE), con la letra de control correcta";
            case TARJETA_SANITARIA -> "16 letras o dígitos";
            case NOMBRE -> "hasta 50 caracteres: letras, espacios, guion o apóstrofo";
            case APELLIDOS -> "hasta 100 caracteres: letras, espacios, guion o apóstrofo";
            case FECHA_NACIMIENTO -> "una fecha dd/mm/aaaa entre el 01/01/1900 y hoy";
            case SEXO -> "Hombre o Mujer";
            case TELEFONO -> "9 dígitos empezando por 6, 7, 8 o 9";
            case CORREO -> "una dirección como usuario@dominio.es, de hasta 100 caracteres";
            case DIRECCION -> "hasta 200 caracteres";
        };
    }

    /** Mensaje para un código de error y, si lo hay, el campo al que se refiere. */
    public static String mensaje(CodigoError codigo, CampoPaciente campo) {
        return switch (codigo) {
            case CAMPO_OBLIGATORIO -> campo == null
                    ? "Falta un campo obligatorio."
                    : "Falta el campo obligatorio «" + etiqueta(campo) + "».";
            case CAMPO_NO_VALIDO -> campo == null
                    ? "Hay un campo con un formato no válido."
                    : "El campo «" + etiqueta(campo) + "» no es válido: debe ser " + formatoEsperado(campo) + ".";
            case DNI_DUPLICADO -> "Ya existe un paciente con ese DNI/NIE.";
            case TARJETA_DUPLICADA -> "Ya existe un paciente con ese número de tarjeta sanitaria.";
            case PACIENTE_NO_ENCONTRADO -> "No existe ningún paciente con ese DNI/NIE.";
            case PACIENTE_DE_BAJA -> "El paciente está dado de baja: no se puede modificar ni volver a dar de baja.";
            case PETICION_NO_VALIDA -> "El servidor no ha entendido la petición. Avisa al administrador del sistema.";
            case ERROR_INTERNO -> "Se ha producido un error en el servidor. Inténtalo de nuevo más tarde.";
            case SIN_CONEXION -> "No hay conexión con el servidor. Comprueba que está en marcha e inténtalo de nuevo.";
            case RESPUESTA_NO_VALIDA -> "El servidor ha enviado una respuesta no válida. Avisa al administrador del sistema.";
            // CU13 Gestionar citas médicas (DEC-065)
            case FECHA_PASADA -> "La cita debe empezar después del momento actual.";
            case FUERA_DE_HORARIO -> "La cita debe empezar y terminar de lunes a viernes, entre las 08:00 y las 20:00.";
            case SOLAPE_RECURSO -> "El recurso ya tiene otra cita a esa hora.";
            case SOLAPE_PACIENTE -> "El paciente ya tiene otra cita a esa hora.";
            case CITA_NO_ENCONTRADA -> "No existe esa cita.";
            case CITA_NO_MODIFICABLE -> "La cita ya no se puede modificar: está cancelada o realizada, o ya ha empezado.";
        };
    }

    /**
     * Campo del formulario que hay que señalar para un error, aunque el protocolo no lo indique
     * (por ejemplo, la tarjeta en {@link CodigoError#TARJETA_DUPLICADA}).
     */
    public static CampoPaciente campoAfectado(CodigoError codigo, CampoPaciente campo) {
        if (campo != null) {
            return campo;
        }
        return switch (codigo) {
            case DNI_DUPLICADO -> CampoPaciente.DNI;
            case TARJETA_DUPLICADA -> CampoPaciente.TARJETA_SANITARIA;
            default -> null;
        };
    }
}
