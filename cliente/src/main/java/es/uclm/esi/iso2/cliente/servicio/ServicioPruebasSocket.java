package es.uclm.esi.iso2.cliente.servicio;

import es.uclm.esi.iso2.comun.dominio.FiltroPruebas;
import es.uclm.esi.iso2.comun.dominio.Incidencia;
import es.uclm.esi.iso2.comun.dominio.NuevaIncidencia;
import es.uclm.esi.iso2.comun.dominio.Prueba;
import es.uclm.esi.iso2.comun.dominio.ResumenPrueba;
import es.uclm.esi.iso2.comun.dominio.SolicitudPendiente;
import es.uclm.esi.iso2.comun.protocolo.CodigoError;
import es.uclm.esi.iso2.comun.protocolo.ProtocoloPruebas;
import es.uclm.esi.iso2.comun.protocolo.Respuesta;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

/**
 * Servicio de pruebas que habla con el servidor por sockets TCP (DEC-039, DEC-057 y DEC-078).
 * Abre una conexión por petición; si no puede conectar o el servidor no responde a tiempo, lanza
 * {@link CodigoError#SIN_CONEXION}.
 */
public class ServicioPruebasSocket implements ServicioPruebas {

    public static final int TIEMPO_ESPERA_MS = 5000;

    private final String host;
    private final int puerto;
    private final int tiempoEsperaMs;

    public ServicioPruebasSocket(String host, int puerto) {
        this(host, puerto, TIEMPO_ESPERA_MS);
    }

    public ServicioPruebasSocket(String host, int puerto, int tiempoEsperaMs) {
        this.host = host;
        this.puerto = puerto;
        this.tiempoEsperaMs = tiempoEsperaMs;
    }

    @Override
    public List<SolicitudPendiente> listarPendientes(String idMedico, String dni) {
        return pedir(ProtocoloPruebas.peticionListarPendientes(idMedico, dni),
                ProtocoloPruebas::pendientesDesdeCampos);
    }

    @Override
    public Prueba registrarRealizacion(String idMedico, long idSolicitud, LocalDateTime fechaHora,
            String observaciones, List<NuevaIncidencia> incidencias) {
        return pedir(ProtocoloPruebas.peticionRegistrar(idMedico, idSolicitud, fechaHora, observaciones, incidencias),
                ProtocoloPruebas::pruebaDesdeCampos);
    }

    @Override
    public Prueba registrarCancelacion(String idMedico, long idSolicitud, String motivo) {
        return pedir(ProtocoloPruebas.peticionCancelar(idMedico, idSolicitud, motivo),
                ProtocoloPruebas::pruebaDesdeCampos);
    }

    @Override
    public Incidencia anadirIncidencia(String idMedico, long idPrueba, NuevaIncidencia incidencia) {
        return pedir(ProtocoloPruebas.peticionAnadirIncidencia(idMedico, idPrueba, incidencia),
                campos -> ProtocoloPruebas.incidenciaDesdeCampos(campos, 0, idPrueba));
    }

    @Override
    public List<ResumenPrueba> buscar(String idMedico, FiltroPruebas filtro) {
        return pedir(ProtocoloPruebas.peticionBuscar(idMedico, filtro), ProtocoloPruebas::resumenesDesdeCampos);
    }

    @Override
    public Prueba obtener(String idMedico, long idPrueba) {
        return pedir(ProtocoloPruebas.peticionObtener(idMedico, idPrueba), ProtocoloPruebas::pruebaDesdeCampos);
    }

    public String host() {
        return host;
    }

    public int puerto() {
        return puerto;
    }

    /** Envía la petición, interpreta la respuesta y convierte los datos con {@code lector}. */
    private <T> T pedir(String peticion, Function<List<String>, T> lector) {
        Respuesta respuesta;
        try {
            respuesta = Respuesta.leer(enviar(peticion));
        } catch (IllegalArgumentException e) {
            throw new ExcepcionServicioPruebas(CodigoError.RESPUESTA_NO_VALIDA, null, e);
        }
        if (!respuesta.correcta()) {
            throw new ExcepcionServicioPruebas(respuesta.codigo(), respuesta.detalle(), null);
        }
        try {
            return lector.apply(respuesta.datos());
        } catch (IllegalArgumentException e) {
            throw new ExcepcionServicioPruebas(CodigoError.RESPUESTA_NO_VALIDA, null, e);
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
            throw new ExcepcionServicioPruebas(CodigoError.SIN_CONEXION, null, e);
        }
    }
}
