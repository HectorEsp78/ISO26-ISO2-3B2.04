package es.uclm.esi.iso2.servidor.citas;

import es.uclm.esi.iso2.comun.dominio.CampoPaciente;
import es.uclm.esi.iso2.comun.dominio.EstadoPaciente;
import es.uclm.esi.iso2.comun.protocolo.CodigoError;
import es.uclm.esi.iso2.servidor.pacientes.PacienteException;

/**
 * Versión simulada de {@code comprobar(dni)} de CU12 (DEC-064), con unos pocos DNI de ejemplo.
 */
final class PacientesSimulados implements ComprobacionPaciente {

    static final String ACTIVO = "12345678Z";
    static final String OTRO_ACTIVO = "X1234567L";
    static final String DE_BAJA = "87654321X";
    /** DNI con formato correcto que no está registrado. */
    static final String NO_EXISTE = "11111111H";
    /** DNI con formato correcto que el servicio de pacientes rechaza como no válido. */
    static final String RECHAZADO = "22222222J";

    @Override
    public EstadoPaciente comprobar(String dni) {
        return switch (dni) {
            case ACTIVO, OTRO_ACTIVO -> EstadoPaciente.ACTIVO;
            case DE_BAJA -> EstadoPaciente.BAJA;
            case RECHAZADO -> throw new PacienteException(CodigoError.CAMPO_NO_VALIDO, CampoPaciente.DNI);
            default -> throw new PacienteException(CodigoError.PACIENTE_NO_ENCONTRADO);
        };
    }
}
