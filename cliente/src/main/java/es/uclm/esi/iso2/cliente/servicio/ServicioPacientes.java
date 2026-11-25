package es.uclm.esi.iso2.cliente.servicio;

import es.uclm.esi.iso2.comun.dominio.Paciente;

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
}
