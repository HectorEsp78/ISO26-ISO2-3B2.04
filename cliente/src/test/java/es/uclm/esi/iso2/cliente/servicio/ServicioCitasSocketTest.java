package es.uclm.esi.iso2.cliente.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import es.uclm.esi.iso2.comun.dominio.Cita;
import es.uclm.esi.iso2.comun.dominio.DatosCita;
import es.uclm.esi.iso2.comun.dominio.EstadoCita;
import es.uclm.esi.iso2.comun.protocolo.CodigoError;
import es.uclm.esi.iso2.comun.protocolo.Protocolo;
import es.uclm.esi.iso2.comun.protocolo.ProtocoloCitas;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * Prueba el servicio de citas por sockets contra un servidor falso que responde una línea fija
 * (igual que {@code ServicioPacientesSocketTest}).
 */
class ServicioCitasSocketTest {

    private static final LocalDateTime INICIO = LocalDateTime.of(2026, 11, 30, 10, 0);
    private static final Cita CITA = new Cita(7, "12345678Z", 1, 2, INICIO, 15, EstadoCita.PROGRAMADA, "");

    private ServerSocket servidor;
    private final CompletableFuture<String> peticionRecibida = new CompletableFuture<>();

    /** Arranca un servidor que atiende una conexión y responde la línea indicada (o nada si es null). */
    private ServicioCitasSocket servicioQueResponde(String respuesta) throws IOException {
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
        return new ServicioCitasSocket("localhost", servidor.getLocalPort(), 2000);
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
    void asignar() throws Exception {
        DatosCita datos = new DatosCita("12345678Z", 1, 2, INICIO, null);
        ServicioCitasSocket servicio = servicioQueResponde(ProtocoloCitas.respuestaOk(CITA));
        assertEquals(CITA, servicio.asignar(datos));
        assertEquals(ProtocoloCitas.peticionAsignar(datos), peticion());
    }

    @Test
    void buscar() throws Exception {
        Cita otra = new Cita(8, "X1234567L", 2, 2, INICIO.plusHours(1), 30, EstadoCita.CANCELADA, "Motivo; con punto y coma");
        ServicioCitasSocket servicio = servicioQueResponde(ProtocoloCitas.respuestaCitas(List.of(CITA, otra)));
        LocalDate dia = INICIO.toLocalDate();
        assertEquals(List.of(CITA, otra), servicio.buscar("12345678Z", dia, dia, EstadoCita.PROGRAMADA));
        assertEquals(ProtocoloCitas.peticionBuscar("12345678Z", dia, dia, EstadoCita.PROGRAMADA), peticion());
    }

    @Test
    void reprogramar() throws Exception {
        ServicioCitasSocket servicio = servicioQueResponde(ProtocoloCitas.respuestaOk(CITA));
        assertEquals(CITA, servicio.reprogramar(7, 2, INICIO, 15));
        assertEquals(ProtocoloCitas.peticionReprogramar(7, 2, INICIO, 15), peticion());
    }

    @Test
    void cancelar() throws Exception {
        Cita cancelada = CITA.conEstado(EstadoCita.CANCELADA, "No puede venir");
        ServicioCitasSocket servicio = servicioQueResponde(ProtocoloCitas.respuestaOk(cancelada));
        assertEquals(cancelada, servicio.cancelar(7, "No puede venir"));
        assertEquals(ProtocoloCitas.peticionCancelar(7, "No puede venir"), peticion());
    }

    @Test
    void listarTipos() throws Exception {
        ServicioCitasSocket servicio = servicioQueResponde(
                ProtocoloCitas.respuestaTipos(ServicioCitasEnMemoria.TIPOS_EJEMPLO));
        assertEquals(ServicioCitasEnMemoria.TIPOS_EJEMPLO, servicio.listarTiposPrueba());
        assertEquals(ProtocoloCitas.LISTAR_TIPOS_PRUEBA, peticion());
    }

    @Test
    void listarRecursos() throws Exception {
        ServicioCitasSocket servicio = servicioQueResponde(
                ProtocoloCitas.respuestaRecursos(ServicioCitasEnMemoria.RECURSOS_EJEMPLO));
        assertEquals(ServicioCitasEnMemoria.RECURSOS_EJEMPLO, servicio.listarRecursos());
        assertEquals(ProtocoloCitas.LISTAR_RECURSOS, peticion());
    }

    @Test
    void errorConDetalle() throws Exception {
        ServicioCitasSocket servicio = servicioQueResponde(ProtocoloCitas.respuestaError(CodigoError.SOLAPE_RECURSO, "17"));
        ExcepcionServicioCitas e = assertThrows(ExcepcionServicioCitas.class, () -> servicio.cancelar(7, "Motivo"));
        assertEquals(CodigoError.SOLAPE_RECURSO, e.codigo());
        assertEquals("17", e.detalle());
        assertEquals("SOLAPE_RECURSO (17)", e.getMessage());
    }

    @Test
    void errorSinDetalle() throws Exception {
        ServicioCitasSocket servicio = servicioQueResponde(ProtocoloCitas.respuestaError(CodigoError.CITA_NO_ENCONTRADA, null));
        ExcepcionServicioCitas e = assertThrows(ExcepcionServicioCitas.class, () -> servicio.cancelar(7, "Motivo"));
        assertEquals(CodigoError.CITA_NO_ENCONTRADA, e.codigo());
        assertNull(e.detalle());
    }

    @Test
    void respuestaQueNoSigueElProtocolo() throws Exception {
        ServicioCitasSocket servicio = servicioQueResponde("HOLA");
        ExcepcionServicioCitas e = assertThrows(ExcepcionServicioCitas.class, servicio::listarRecursos);
        assertEquals(CodigoError.RESPUESTA_NO_VALIDA, e.codigo());
    }

    @Test
    void respuestaCorrectaConDatosMalFormados() throws Exception {
        ServicioCitasSocket servicio = servicioQueResponde(Protocolo.componer(Protocolo.OK, "2", "1", "Sala"));
        ExcepcionServicioCitas e = assertThrows(ExcepcionServicioCitas.class, servicio::listarRecursos);
        assertEquals(CodigoError.RESPUESTA_NO_VALIDA, e.codigo());
    }

    @Test
    void elServidorCierraSinResponder() throws Exception {
        ServicioCitasSocket servicio = servicioQueResponde(null);
        ExcepcionServicioCitas e = assertThrows(ExcepcionServicioCitas.class, servicio::listarTiposPrueba);
        assertEquals(CodigoError.SIN_CONEXION, e.codigo());
    }

    @Test
    void sinServidor() throws Exception {
        int puertoLibre;
        try (ServerSocket s = new ServerSocket(0)) {
            puertoLibre = s.getLocalPort();
        }
        ServicioCitasSocket servicio = new ServicioCitasSocket("localhost", puertoLibre);
        assertEquals("localhost", servicio.host());
        assertEquals(puertoLibre, servicio.puerto());
        ExcepcionServicioCitas e = assertThrows(ExcepcionServicioCitas.class, servicio::listarTiposPrueba);
        assertEquals(CodigoError.SIN_CONEXION, e.codigo());
    }
}
