package es.uclm.esi.iso2.servidor.citas;

import es.uclm.esi.iso2.comun.dominio.EstadoPaciente;
import es.uclm.esi.iso2.servidor.pacientes.PacienteException;

/**
 * Comprueba si un paciente existe y está activo (RN-01, DEC-064).
 *
 * <p>En el servidor real es {@code IPacienteService::comprobar} de CU12 (DEC-061). En las
 * pruebas se usa una versión simulada con unos pocos DNI de ejemplo.</p>
 */
@FunctionalInterface
public interface ComprobacionPaciente {

    /**
     * @return el estado del paciente
     * @throws PacienteException con {@code PACIENTE_NO_ENCONTRADO} si no existe, o con
     *                           {@code CAMPO_OBLIGATORIO}/{@code CAMPO_NO_VALIDO} si el DNI está mal
     */
    EstadoPaciente comprobar(String dni);
}
