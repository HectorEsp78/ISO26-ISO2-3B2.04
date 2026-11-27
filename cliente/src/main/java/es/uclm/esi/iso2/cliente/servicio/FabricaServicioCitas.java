package es.uclm.esi.iso2.cliente.servicio;

import java.util.function.UnaryOperator;

/**
 * Crea el servicio de citas con la misma configuración que el de pacientes ({@link FabricaServicio}):
 * {@code radiodiagnostico.modo}, {@code radiodiagnostico.host} y {@code radiodiagnostico.puerto}.
 *
 * <p>Para no repetir la lectura y la comprobación de la configuración, se pide primero el servicio
 * de pacientes y se usa el mismo servidor (o el modo sin servidor) para las citas.</p>
 */
public final class FabricaServicioCitas {

    private FabricaServicioCitas() {
    }

    /**
     * @throws IllegalArgumentException si el modo o el puerto no son válidos
     */
    public static ServicioCitas crear(UnaryOperator<String> propiedades) {
        ServicioPacientes pacientes = FabricaServicio.crear(propiedades);
        if (pacientes instanceof ServicioPacientesSocket socket) {
            return new ServicioCitasSocket(socket.host(), socket.puerto());
        }
        return ServicioCitasEnMemoria.conDatosDeEjemplo();
    }
}
