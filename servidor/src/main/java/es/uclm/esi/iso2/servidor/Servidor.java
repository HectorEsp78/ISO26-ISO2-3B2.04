package es.uclm.esi.iso2.servidor;

import es.uclm.esi.iso2.servidor.citas.CitaDAO;
import es.uclm.esi.iso2.servidor.citas.CitaService;
import es.uclm.esi.iso2.servidor.citas.ComprobacionPaciente;
import es.uclm.esi.iso2.servidor.citas.ICitaService;
import es.uclm.esi.iso2.servidor.pacientes.CancelacionCitas;
import es.uclm.esi.iso2.servidor.pacientes.IPacienteService;
import es.uclm.esi.iso2.servidor.pacientes.PacienteDAO;
import es.uclm.esi.iso2.servidor.pacientes.PacienteService;
import es.uclm.esi.iso2.servidor.persistencia.FabricaConexiones;
import es.uclm.esi.iso2.servidor.red.ManejadorPeticiones;
import es.uclm.esi.iso2.servidor.red.OperacionesCitas;
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
        PacienteDAO pacienteDao = new PacienteDAO(conexiones);
        // CU13 comprueba los pacientes con comprobar(dni) de CU12 (DEC-064). Esa operación no usa la
        // cancelación de citas, así que basta un PacienteService sin ella y se evita que cada
        // servicio necesite al otro para crearse.
        ComprobacionPaciente comprobacion = new PacienteService(pacienteDao, CancelacionCitas.NINGUNA)::comprobar;
        ICitaService citas = new CitaService(new CitaDAO(conexiones), comprobacion);
        // Al dar de baja a un paciente se cancelan sus citas pendientes (DEC-048).
        IPacienteService pacientes = new PacienteService(pacienteDao, citas::cancelarCitasPendientes);

        ManejadorPeticiones manejador = new ManejadorPeticiones();
        OperacionesPacientes.registrar(manejador, pacientes);
        OperacionesCitas.registrar(manejador, citas);

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
