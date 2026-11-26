package es.uclm.esi.iso2.servidor;

import es.uclm.esi.iso2.servidor.pacientes.CancelacionCitas;
import es.uclm.esi.iso2.servidor.pacientes.IPacienteService;
import es.uclm.esi.iso2.servidor.pacientes.PacienteDAO;
import es.uclm.esi.iso2.servidor.pacientes.PacienteService;
import es.uclm.esi.iso2.servidor.persistencia.FabricaConexiones;
import es.uclm.esi.iso2.servidor.red.ManejadorPeticiones;
import es.uclm.esi.iso2.servidor.red.OperacionesPacientes;
import es.uclm.esi.iso2.servidor.red.ServidorSockets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Monta las piezas del servidor: conexiones, servicios y operaciones del protocolo (DEC-068).
 * Cada caso de uso añade aquí sus servicios y registra sus operaciones.
 */
public final class Servidor implements AutoCloseable {

    private final ServidorSockets sockets;
    private final IPacienteService pacientes;

    private Servidor(ServidorSockets sockets, IPacienteService pacientes) {
        this.sockets = sockets;
        this.pacientes = pacientes;
    }

    /**
     * Crea el servidor y comprueba que se puede conectar con la base de datos.
     *
     * @throws SQLException si no se puede conectar con la base de datos
     * @throws IOException  si el puerto está ocupado
     */
    public static Servidor crear(int puerto, FabricaConexiones conexiones) throws SQLException, IOException {
        try (Connection prueba = conexiones.abrir()) {
            // solo comprueba que la base de datos responde
        }
        // Cuando exista CU13: new PacienteService(dao, citaService::cancelarCitasPendientes)
        IPacienteService pacientes = new PacienteService(new PacienteDAO(conexiones), CancelacionCitas.NINGUNA);

        ManejadorPeticiones manejador = new ManejadorPeticiones();
        OperacionesPacientes.registrar(manejador, pacientes);

        return new Servidor(new ServidorSockets(puerto, manejador), pacientes);
    }

    public void arrancar() {
        sockets.arrancar();
    }

    public int puerto() {
        return sockets.puerto();
    }

    /** Servicio de pacientes, para los casos de uso que lo necesiten en el servidor (CU13). */
    public IPacienteService pacientes() {
        return pacientes;
    }

    @Override
    public void close() throws IOException {
        sockets.close();
    }
}
