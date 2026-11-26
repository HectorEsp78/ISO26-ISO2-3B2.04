package es.uclm.esi.iso2.cliente.servicio;

import es.uclm.esi.iso2.comun.dominio.EstadoPaciente;
import es.uclm.esi.iso2.comun.dominio.Paciente;
import es.uclm.esi.iso2.comun.protocolo.CodigoError;

/**
 * Operaciones de CU12 que el cliente pide al servidor (DEC-057 y DEC-059).
 *
 * <p>Todas las operaciones devuelven el paciente tal y como queda guardado y lanzan
 * {@link ExcepcionServicio} si el servidor las rechaza o no se puede contactar con él.</p>
 */
public interface ServicioPacientes {

    /** Da de alta un paciente nuevo (flujo 6.1). Se registra en estado ACTIVO. */
    Paciente alta(Paciente paciente);

    /** Busca un paciente por su DNI/NIE (flujo 6.2, DEC-056). */
    Paciente buscar(String dni);

    /** Modifica los datos de un paciente activo; el DNI lo identifica y no cambia (flujo 6.3, RN-03). */
    Paciente modificar(Paciente paciente);

    /** Da de baja lógica a un paciente activo (flujo 6.4, RN-02). */
    Paciente baja(String dni);

    /**
     * Comprueba si existe un paciente con ese DNI/NIE y devuelve su estado, sin pedir sus datos
     * personales (DEC-061). Pensado para otros casos de uso, como CU13.
     *
     * @throws ExcepcionServicio con {@code PACIENTE_NO_ENCONTRADO} si no existe
     */
    EstadoPaciente comprobar(String dni);

    /**
     * Indica si existe un paciente activo con ese DNI/NIE: {@code false} si no existe o está de
     * baja. Cualquier otro error (DNI no válido, sin conexión...) se lanza como excepción.
     */
    default boolean estaActivo(String dni) {
        try {
            return comprobar(dni) == EstadoPaciente.ACTIVO;
        } catch (ExcepcionServicio e) {
            if (e.codigo() == CodigoError.PACIENTE_NO_ENCONTRADO) {
                return false;
            }
            throw e;
        }
    }
}
