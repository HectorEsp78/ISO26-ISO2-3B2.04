package es.uclm.esi.iso2.servidor.pacientes;

import es.uclm.esi.iso2.comun.dominio.EstadoPaciente;
import es.uclm.esi.iso2.comun.dominio.Paciente;

/**
 * Operaciones de CU12 en el servidor (DEC-068). El resto del servidor, por ejemplo
 * {@code CitaService} de CU13 (DEC-064), solo depende de esta interfaz.
 *
 * <p>Todas validan los datos aunque el cliente ya lo haya hecho, y lanzan
 * {@link PacienteException} con el código de error del protocolo (DEC-057) si se rechazan.</p>
 */
public interface IPacienteService {

    /** Flujo 6.1: registra un paciente nuevo en estado {@code ACTIVO}. */
    Paciente alta(Paciente paciente);

    /** Flujo 6.2: devuelve el paciente con ese DNI/NIE. */
    Paciente buscar(String dni);

    /** Flujo 6.3: guarda los datos de un paciente activo. El DNI no cambia (RN-03). */
    Paciente modificar(Paciente paciente);

    /** Flujo 6.4: baja lógica (RN-02) y cancelación de sus citas pendientes (DEC-048). */
    Paciente baja(String dni);

    /**
     * Estado del paciente, sin sus datos personales (DEC-061). Lo usa CU13 para comprobar que
     * el paciente existe y está activo (DEC-064).
     *
     * @throws PacienteException con {@code PACIENTE_NO_ENCONTRADO} si no existe
     */
    EstadoPaciente comprobar(String dni);
}
