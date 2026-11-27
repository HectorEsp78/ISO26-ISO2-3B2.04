package es.uclm.esi.iso2.cliente.servicio;

import es.uclm.esi.iso2.comun.dominio.Cita;
import es.uclm.esi.iso2.comun.dominio.DatosCita;
import es.uclm.esi.iso2.comun.dominio.EstadoCita;
import es.uclm.esi.iso2.comun.dominio.Recurso;
import es.uclm.esi.iso2.comun.dominio.TipoPrueba;
import es.uclm.esi.iso2.comun.protocolo.CodigoError;
import es.uclm.esi.iso2.comun.protocolo.ProtocoloCitas;
import es.uclm.esi.iso2.comun.protocolo.Respuesta;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

/**
 * Servicio de citas que habla con el servidor por sockets TCP (DEC-039 y DEC-065).
 *
 * <p>Funciona igual que {@link ServicioPacientesSocket}: abre una conexión por petición, envía una
 * línea, lee la respuesta y cierra. Si no puede conectar o el servidor no responde a tiempo, lanza
 * {@link CodigoError#SIN_CONEXION}.</p>
 */
public class ServicioCitasSocket implements ServicioCitas {

    private final String host;
    private final int puerto;
    private final int tiempoEsperaMs;

    public ServicioCitasSocket(String host, int puerto) {
        this(host, puerto, ServicioPacientesSocket.TIEMPO_ESPERA_MS);
    }

    public ServicioCitasSocket(String host, int puerto, int tiempoEsperaMs) {
        this.host = host;
        this.puerto = puerto;
        this.tiempoEsperaMs = tiempoEsperaMs;
    }

    @Override
    public Cita asignar(DatosCita datos) {
        return pedir(ProtocoloCitas.peticionAsignar(datos), campos -> ProtocoloCitas.citaDesdeCampos(campos, 0));
    }

    @Override
    public List<Cita> buscar(String dni, LocalDate desde, LocalDate hasta, EstadoCita estado) {
        return pedir(ProtocoloCitas.peticionBuscar(dni, desde, hasta, estado), ProtocoloCitas::citasDesdeCampos);
    }

    @Override
    public Cita reprogramar(long id, Integer idRecurso, LocalDateTime inicio, Integer duracionMinutos) {
        return pedir(ProtocoloCitas.peticionReprogramar(id, idRecurso, inicio, duracionMinutos),
                campos -> ProtocoloCitas.citaDesdeCampos(campos, 0));
    }

    @Override
    public Cita cancelar(long id, String motivo) {
        return pedir(ProtocoloCitas.peticionCancelar(id, motivo), campos -> ProtocoloCitas.citaDesdeCampos(campos, 0));
    }

    @Override
    public List<TipoPrueba> listarTiposPrueba() {
        return pedir(ProtocoloCitas.LISTAR_TIPOS_PRUEBA, ProtocoloCitas::tiposDesdeCampos);
    }

    @Override
    public List<Recurso> listarRecursos() {
        return pedir(ProtocoloCitas.LISTAR_RECURSOS, ProtocoloCitas::recursosDesdeCampos);
    }

    public String host() {
        return host;
    }

    public int puerto() {
        return puerto;
    }

    /**
     * Envía la petición, comprueba si es un error y convierte los datos de la respuesta.
     *
     * @param conversion pasa los campos que siguen a {@code OK} al objeto que se devuelve
     */
    private <T> T pedir(String peticion, Function<List<String>, T> conversion) {
        Respuesta respuesta;
        try {
            respuesta = Respuesta.leer(enviar(peticion));
        } catch (IllegalArgumentException e) {
            throw new ExcepcionServicioCitas(CodigoError.RESPUESTA_NO_VALIDA, null, e);
        }
        if (!respuesta.correcta()) {
            throw new ExcepcionServicioCitas(respuesta.codigo(), respuesta.detalle());
        }
        try {
            return conversion.apply(respuesta.datos());
        } catch (RuntimeException e) {
            throw new ExcepcionServicioCitas(CodigoError.RESPUESTA_NO_VALIDA, null, e);
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
            throw new ExcepcionServicioCitas(CodigoError.SIN_CONEXION, null, e);
        }
    }
}
