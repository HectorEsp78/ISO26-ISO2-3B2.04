package es.uclm.esi.iso2.servidor.red;

import es.uclm.esi.iso2.comun.dominio.Paciente;
import es.uclm.esi.iso2.comun.protocolo.Protocolo;
import es.uclm.esi.iso2.servidor.pacientes.IPacienteService;
import es.uclm.esi.iso2.servidor.pacientes.PacienteException;

import java.util.List;
import java.util.function.Supplier;

/**
 * Operaciones del protocolo de CU12 (DEC-057 y DEC-061): convierten los campos recibidos en
 * llamadas a {@link IPacienteService} y su resultado en la línea de respuesta.
 */
public final class OperacionesPacientes {

    private OperacionesPacientes() {
    }

    /** Registra en el manejador las operaciones de pacientes. */
    public static void registrar(ManejadorPeticiones manejador, IPacienteService servicio) {
        manejador.registrar(Protocolo.ALTA_PACIENTE,
                a -> paciente(() -> servicio.alta(Protocolo.pacienteDesdeDatos(a, 0))));
        manejador.registrar(Protocolo.BUSCAR_PACIENTE,
                a -> paciente(() -> servicio.buscar(dni(a))));
        manejador.registrar(Protocolo.MODIFICAR_PACIENTE,
                a -> paciente(() -> servicio.modificar(Protocolo.pacienteDesdeDatos(a, 0))));
        manejador.registrar(Protocolo.BAJA_PACIENTE,
                a -> paciente(() -> servicio.baja(dni(a))));
        manejador.registrar(Protocolo.COMPROBAR_PACIENTE, a -> {
            String dni = dni(a);
            try {
                return Protocolo.respuestaOk(servicio.comprobar(dni));
            } catch (PacienteException e) {
                return Protocolo.respuestaError(e.codigo(), e.campo());
            }
        });
    }

    private static String paciente(Supplier<Paciente> operacion) {
        try {
            return Protocolo.respuestaOk(operacion.get());
        } catch (PacienteException e) {
            return Protocolo.respuestaError(e.codigo(), e.campo());
        }
    }

    /** Las operaciones que reciben un DNI deben tener exactamente un argumento. */
    private static String dni(List<String> argumentos) {
        if (argumentos.size() != 1) {
            throw new IllegalArgumentException("Se esperaba un único argumento (DNI)");
        }
        return argumentos.get(0);
    }
}
