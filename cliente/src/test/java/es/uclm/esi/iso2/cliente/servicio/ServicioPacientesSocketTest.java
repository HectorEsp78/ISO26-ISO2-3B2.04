package es.uclm.esi.iso2.cliente.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import es.uclm.esi.iso2.comun.dominio.CampoPaciente;
import es.uclm.esi.iso2.comun.dominio.EstadoPaciente;
import es.uclm.esi.iso2.comun.dominio.Paciente;
import es.uclm.esi.iso2.comun.protocolo.CodigoError;
import es.uclm.esi.iso2.comun.protocolo.Protocolo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * Prueba el servicio por sockets contra un servidor falso que responde una línea fija.
 */
class ServicioPacientesSocketTest {

    private static final Paciente LUCIA = ServicioPacientesEnMemoriaTest.LUCIA;

    private ServerSocket servidor;
    private final CompletableFuture<String> peticionRecibida = new CompletableFuture<>();

    /** Arranca un servidor que atiende una conexión y responde la línea indicada (o nada si es null). */
    private ServicioPacientesSocket servicioQueResponde(String respuesta) throws IOException {
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
        return new ServicioPacientesSocket("localhost", servidor.getLocalPort(), 2000);
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

    private static void assertError(CodigoError codigo, CampoPaciente campo, ExcepcionServicio e) {
        assertEquals(codigo, e.codigo());
        assertEquals(campo, e.campo());
    }

    @Test
    void altaCorrecta() throws Exception {
        ServicioPacientesSocket servicio = servicioQueResponde(Protocolo.respuestaOk(LUCIA));
        assertEquals(LUCIA, servicio.alta(LUCIA));
        assertEquals(Protocolo.peticionAlta(LUCIA), peticion());
    }

    @Test
    void busquedaCorrecta() throws Exception {
        ServicioPacientesSocket servicio = servicioQueResponde(Protocolo.respuestaOk(LUCIA));
        assertEquals(LUCIA, servicio.buscar("12345678Z"));
        assertEquals("BUSCAR_PACIENTE;12345678Z", peticion());
    }

    @Test
    void modificacionCorrecta() throws Exception {
        ServicioPacientesSocket servicio = servicioQueResponde(Protocolo.respuestaOk(LUCIA));
        assertEquals(LUCIA, servicio.modificar(LUCIA));
        assertEquals(Protocolo.peticionModificar(LUCIA), peticion());
    }

    @Test
    void bajaCorrecta() throws Exception {
        Paciente deBaja = LUCIA.conEstado(EstadoPaciente.BAJA);
        ServicioPacientesSocket servicio = servicioQueResponde(Protocolo.respuestaOk(deBaja));
        assertEquals(deBaja, servicio.baja("12345678Z"));
        assertEquals("BAJA_PACIENTE;12345678Z", peticion());
    }

    @Test
    void errorConCampo() throws Exception {
        ServicioPacientesSocket servicio = servicioQueResponde("ERROR;CAMPO_OBLIGATORIO;telefono");
        assertError(CodigoError.CAMPO_OBLIGATORIO, CampoPaciente.TELEFONO,
                assertThrows(ExcepcionServicio.class, () -> servicio.alta(LUCIA)));
    }

    @Test
    void errorSinCampo() throws Exception {
        ServicioPacientesSocket servicio = servicioQueResponde("ERROR;PACIENTE_NO_ENCONTRADO");
        assertError(CodigoError.PACIENTE_NO_ENCONTRADO, null,
                assertThrows(ExcepcionServicio.class, () -> servicio.buscar("12345678Z")));
    }

    @Test
    void respuestaQueNoSigueElProtocolo() throws Exception {
        ServicioPacientesSocket servicio = servicioQueResponde("HOLA");
        assertError(CodigoError.RESPUESTA_NO_VALIDA, null,
                assertThrows(ExcepcionServicio.class, () -> servicio.buscar("12345678Z")));
    }

    @Test
    void respuestaCorrectaConDatosIncompletos() throws Exception {
        ServicioPacientesSocket servicio = servicioQueResponde("OK;12345678Z");
        assertError(CodigoError.RESPUESTA_NO_VALIDA, null,
                assertThrows(ExcepcionServicio.class, () -> servicio.buscar("12345678Z")));
    }

    @Test
    void elServidorCierraSinResponder() throws Exception {
        ServicioPacientesSocket servicio = servicioQueResponde(null);
        assertError(CodigoError.SIN_CONEXION, null,
                assertThrows(ExcepcionServicio.class, () -> servicio.buscar("12345678Z")));
    }

    @Test
    void sinServidor() throws Exception {
        int puertoLibre;
        try (ServerSocket temporal = new ServerSocket(0)) {
            puertoLibre = temporal.getLocalPort();
        }
        ServicioPacientesSocket servicio = new ServicioPacientesSocket("localhost", puertoLibre);
        assertEquals("localhost", servicio.host());
        assertEquals(puertoLibre, servicio.puerto());
        assertError(CodigoError.SIN_CONEXION, null,
                assertThrows(ExcepcionServicio.class, () -> servicio.buscar("12345678Z")));
    }

    @Test
    void elServidorNoRespondeATiempo() throws Exception {
        servidor = new ServerSocket(0);
        // Acepta la conexión (cola del sistema) pero nunca responde
        ServicioPacientesSocket servicio = new ServicioPacientesSocket("localhost", servidor.getLocalPort(), 300);
        assertError(CodigoError.SIN_CONEXION, null,
                assertThrows(ExcepcionServicio.class, () -> servicio.buscar("12345678Z")));
    }
}
