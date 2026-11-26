package es.uclm.esi.iso2.cliente.servicio;

import es.uclm.esi.iso2.comun.dominio.EstadoPaciente;
import es.uclm.esi.iso2.comun.dominio.Paciente;
import es.uclm.esi.iso2.comun.protocolo.CodigoError;
import es.uclm.esi.iso2.comun.protocolo.Protocolo;
import es.uclm.esi.iso2.comun.protocolo.Respuesta;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Servicio de pacientes que habla con el servidor por sockets TCP (DEC-039 y DEC-057).
 *
 * <p>Abre una conexión por petición: envía una línea, lee la respuesta y cierra. Si no puede
 * conectar o el servidor no responde a tiempo, lanza {@link CodigoError#SIN_CONEXION}.</p>
 */
public class ServicioPacientesSocket implements ServicioPacientes {

    public static final int TIEMPO_ESPERA_MS = 5000;

    private final String host;
    private final int puerto;
    private final int tiempoEsperaMs;

    public ServicioPacientesSocket(String host, int puerto) {
        this(host, puerto, TIEMPO_ESPERA_MS);
    }

    public ServicioPacientesSocket(String host, int puerto, int tiempoEsperaMs) {
        this.host = host;
        this.puerto = puerto;
        this.tiempoEsperaMs = tiempoEsperaMs;
    }

    @Override
    public Paciente alta(Paciente paciente) {
        return pedirPaciente(Protocolo.peticionAlta(paciente));
    }

    @Override
    public Paciente buscar(String dni) {
        return pedirPaciente(Protocolo.peticionBuscar(dni));
    }

    @Override
    public Paciente modificar(Paciente paciente) {
        return pedirPaciente(Protocolo.peticionModificar(paciente));
    }

    @Override
    public Paciente baja(String dni) {
        return pedirPaciente(Protocolo.peticionBaja(dni));
    }

    @Override
    public EstadoPaciente comprobar(String dni) {
        Respuesta respuesta = interpretar(enviar(Protocolo.peticionComprobar(dni)));
        lanzarSiEsError(respuesta);
        try {
            return Protocolo.estadoDesdeCampos(respuesta.datos());
        } catch (IllegalArgumentException e) {
            throw new ExcepcionServicio(CodigoError.RESPUESTA_NO_VALIDA, null, e);
        }
    }

    public String host() {
        return host;
    }

    public int puerto() {
        return puerto;
    }

    private Paciente pedirPaciente(String peticion) {
        Respuesta respuesta = interpretar(enviar(peticion));
        lanzarSiEsError(respuesta);
        try {
            return Protocolo.pacienteDesdeCampos(respuesta.datos(), 0);
        } catch (IllegalArgumentException e) {
            throw new ExcepcionServicio(CodigoError.RESPUESTA_NO_VALIDA, null, e);
        }
    }

    private static void lanzarSiEsError(Respuesta respuesta) {
        if (!respuesta.correcta()) {
            throw new ExcepcionServicio(respuesta.codigo(), respuesta.campo());
        }
    }

    private static Respuesta interpretar(String linea) {
        try {
            return Respuesta.leer(linea);
        } catch (IllegalArgumentException e) {
            throw new ExcepcionServicio(CodigoError.RESPUESTA_NO_VALIDA, null, e);
        }
    }

    /** Envía una petición y devuelve la línea de respuesta. */
    String enviar(String peticion) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, puerto), tiempoEsperaMs);
            socket.setSoTimeout(tiempoEsperaMs);
            PrintWriter salida = new PrintWriter(
                    new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
            BufferedReader entrada = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            salida.println(peticion);
            String linea = entrada.readLine();
            if (linea == null) {
                throw new IOException("El servidor ha cerrado la conexión sin responder");
            }
            return linea;
        } catch (IOException e) {
            throw new ExcepcionServicio(CodigoError.SIN_CONEXION, null, e);
        }
    }
}
