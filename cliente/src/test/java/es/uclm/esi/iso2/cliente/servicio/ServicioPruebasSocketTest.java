package es.uclm.esi.iso2.cliente.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import es.uclm.esi.iso2.comun.dominio.EstadoPrueba;
import es.uclm.esi.iso2.comun.dominio.FiltroPruebas;
import es.uclm.esi.iso2.comun.dominio.Incidencia;
import es.uclm.esi.iso2.comun.dominio.NuevaIncidencia;
import es.uclm.esi.iso2.comun.dominio.Prueba;
import es.uclm.esi.iso2.comun.dominio.SolicitudPendiente;
import es.uclm.esi.iso2.comun.dominio.TipoIncidencia;
import es.uclm.esi.iso2.comun.protocolo.CodigoError;
import es.uclm.esi.iso2.comun.protocolo.ProtocoloPruebas;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/** Prueba el servicio por sockets contra un servidor falso que responde una línea fija. */
class ServicioPruebasSocketTest {

    private static final LocalDateTime FECHA = LocalDateTime.of(2026, 11, 26, 9, 30);
    private static final Prueba PRUEBA = new Prueba(7, 4, "12345678Z", "medico1", EstadoPrueba.REALIZADA, FECHA, "obs",
            "", LocalDateTime.of(2026, 11, 26, 10, 0, 5), List.of());

    private ServerSocket servidor;
    private final CompletableFuture<String> peticionRecibida = new CompletableFuture<>();

    private ServicioPruebasSocket servicioQueResponde(String respuesta) throws IOException {
        servidor = new ServerSocket(0);
        Thread hilo = new Thread(() -> {
            try (Socket s = servidor.accept()) {
                BufferedReader entrada = new BufferedReader(
                        new InputStreamReader(s.getInputStream(), StandardCharsets.UTF_8));
                peticionRecibida.complete(entrada.readLine());
                if (respuesta != null) {
                    PrintWriter salida = new PrintWriter(
                            new OutputStreamWriter(s.getOutputStream(), StandardCharsets.UTF_8), true);
                    salida.println(respuesta);
                }
            } catch (IOException e) {
                peticionRecibida.completeExceptionally(e);
            }
        });
        hilo.setDaemon(true);
        hilo.start();
        return new ServicioPruebasSocket("localhost", servidor.getLocalPort(), 2000);
    }

    @AfterEach
    void cerrar() throws IOException {
        if (servidor != null) {
            servidor.close();
        }
    }

    private String peticion() throws Exception {
        return peticionRecibida.get(2, TimeUnit.SECONDS);
    }

    @Test
    void listarPendientes() throws Exception {
        SolicitudPendiente p = new SolicitudPendiente(4, "12345678Z", "Lucía", "Rx", "NORMAL", 9,
                LocalDateTime.of(2026, 12, 1, 10, 0));
        ServicioPruebasSocket servicio = servicioQueResponde(ProtocoloPruebas.respuestaPendientes(List.of(p)));
        assertEquals(List.of(p), servicio.listarPendientes("medico1", "12345678Z"));
        assertEquals("LISTAR_SOLICITUDES_PENDIENTES;medico1;12345678Z", peticion());
    }

    @Test
    void registrarRealizacion() throws Exception {
        ServicioPruebasSocket servicio = servicioQueResponde(ProtocoloPruebas.respuestaOk(PRUEBA));
        List<NuevaIncidencia> incidencias = List.of(new NuevaIncidencia(TipoIncidencia.OTRA, "x"));
        assertEquals(PRUEBA, servicio.registrarRealizacion("medico1", 4, FECHA, "obs", incidencias));
        assertEquals(ProtocoloPruebas.peticionRegistrar("medico1", 4, FECHA, "obs", incidencias), peticion());
    }

    @Test
    void registrarCancelacion() throws Exception {
        ServicioPruebasSocket servicio = servicioQueResponde(ProtocoloPruebas.respuestaOk(PRUEBA));
        assertEquals(PRUEBA, servicio.registrarCancelacion("medico1", 4, "Ausente"));
        assertEquals("CANCELAR_PRUEBA;medico1;4;Ausente", peticion());
    }

    @Test
    void anadirIncidencia() throws Exception {
        Incidencia i = new Incidencia(3, 7, TipoIncidencia.PACIENTE, "Se movió", LocalDateTime.of(2026, 11, 26, 10, 1, 2));
        ServicioPruebasSocket servicio = servicioQueResponde(ProtocoloPruebas.respuestaOk(i));
        assertEquals(i, servicio.anadirIncidencia("medico1", 7, new NuevaIncidencia(TipoIncidencia.PACIENTE, "Se movió")));
        assertEquals("ANADIR_INCIDENCIA;medico1;7;PACIENTE;Se movió", peticion());
    }

    @Test
    void buscar() throws Exception {
        ServicioPruebasSocket servicio = servicioQueResponde(ProtocoloPruebas.respuestaResumenes(
                List.of(PRUEBA.resumen())));
        assertEquals(List.of(PRUEBA.resumen()), servicio.buscar("medico1", FiltroPruebas.SIN_FILTROS));
        assertEquals("BUSCAR_PRUEBAS;medico1;;;;;", peticion());
    }

    @Test
    void obtener() throws Exception {
        ServicioPruebasSocket servicio = servicioQueResponde(ProtocoloPruebas.respuestaOk(PRUEBA));
        assertEquals(PRUEBA, servicio.obtener("medico1", 7));
        assertEquals("OBTENER_PRUEBA;medico1;7", peticion());
    }

    @Test
    void erroresDelServidor() throws Exception {
        ExcepcionServicioPruebas e = assertThrows(ExcepcionServicioPruebas.class,
                () -> servicioQueResponde("ERROR;PRUEBA_YA_EXISTE;9").obtener("medico1", 7));
        assertEquals(CodigoError.PRUEBA_YA_EXISTE, e.codigo());
        assertEquals("9", e.detalle());
    }

    @Test
    void respuestasQueNoSigueElProtocolo() throws Exception {
        assertEquals(CodigoError.RESPUESTA_NO_VALIDA, assertThrows(ExcepcionServicioPruebas.class,
                () -> servicioQueResponde("HOLA").obtener("medico1", 7)).codigo());
        cerrar();
        assertEquals(CodigoError.RESPUESTA_NO_VALIDA, assertThrows(ExcepcionServicioPruebas.class,
                () -> servicioQueResponde("OK;1;2").obtener("medico1", 7)).codigo());
    }

    @Test
    void sinConexion() throws Exception {
        ServicioPruebasSocket servicio = servicioQueResponde(null);
        assertEquals(CodigoError.SIN_CONEXION, assertThrows(ExcepcionServicioPruebas.class,
                () -> servicio.obtener("medico1", 7)).codigo());
        cerrar();
        ServicioPruebasSocket cerrado = new ServicioPruebasSocket("localhost", servidor.getLocalPort(), 500);
        assertEquals(CodigoError.SIN_CONEXION, assertThrows(ExcepcionServicioPruebas.class,
                () -> cerrado.obtener("medico1", 7)).codigo());
    }
}
