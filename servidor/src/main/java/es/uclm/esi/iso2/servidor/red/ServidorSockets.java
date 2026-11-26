package es.uclm.esi.iso2.servidor.red;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Servidor TCP (DEC-039): acepta conexiones y atiende cada cliente en un hilo propio. Por cada
 * línea recibida responde una línea, hasta que el cliente cierra la conexión.
 */
public class ServidorSockets implements AutoCloseable {

    private static final System.Logger LOG = System.getLogger(ServidorSockets.class.getName());

    private final ServerSocket socketServidor;
    private final ManejadorPeticiones manejador;
    private Thread hiloAceptador;

    /**
     * Abre el puerto. Con el puerto 0 el sistema elige uno libre (útil en las pruebas).
     *
     * @throws IOException si el puerto está ocupado
     */
    public ServidorSockets(int puerto, ManejadorPeticiones manejador) throws IOException {
        this.socketServidor = new ServerSocket(puerto);
        this.manejador = manejador;
    }

    /** Puerto en el que escucha. */
    public int puerto() {
        return socketServidor.getLocalPort();
    }

    /** Empieza a aceptar clientes en segundo plano. */
    public synchronized void arrancar() {
        if (hiloAceptador != null) {
            throw new IllegalStateException("El servidor ya está arrancado");
        }
        hiloAceptador = new Thread(this::aceptar, "servidor-aceptador");
        hiloAceptador.start();
    }

    /** Deja de aceptar clientes. Las conexiones abiertas terminan cuando su cliente las cierra. */
    @Override
    public void close() throws IOException {
        socketServidor.close();
    }

    private void aceptar() {
        while (!socketServidor.isClosed()) {
            try {
                Socket cliente = socketServidor.accept();
                Thread hilo = new Thread(() -> atender(cliente), "cliente-" + cliente.getRemoteSocketAddress());
                hilo.setDaemon(true);
                hilo.start();
            } catch (IOException e) {
                if (!socketServidor.isClosed()) {
                    LOG.log(System.Logger.Level.WARNING, "Error al aceptar una conexión", e);
                }
            }
        }
    }

    private void atender(Socket cliente) {
        try (cliente;
             BufferedReader entrada = new BufferedReader(
                     new InputStreamReader(cliente.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter salida = new PrintWriter(
                     new OutputStreamWriter(cliente.getOutputStream(), StandardCharsets.UTF_8), true)) {
            String linea;
            while ((linea = entrada.readLine()) != null) {
                salida.println(manejador.atender(linea));
            }
        } catch (IOException e) {
            LOG.log(System.Logger.Level.DEBUG, "Conexión cerrada con error", e);
        }
    }
}
