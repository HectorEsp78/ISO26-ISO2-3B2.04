package es.uclm.esi.iso2.servidor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import es.uclm.esi.iso2.servidor.persistencia.FabricaConexiones;
import es.uclm.esi.iso2.servidor.persistencia.ScriptSql;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

import org.junit.jupiter.api.Test;

/**
 * Prueba de integración de CU13 con el servidor completo: sockets, servicio de pacientes real de
 * CU12 sobre H2 y servicio de citas.
 */
class ServidorCitasTest {

    @Test
    void asignarPorSocketYCancelarCitasAlDarDeBajaAlPaciente() throws Exception {
        FabricaConexiones conexiones = FabricaConexiones.jdbc(
                "jdbc:h2:mem:servidorcitas;MODE=MySQL;DB_CLOSE_DELAY=-1", "sa", "");
        ScriptSql.ejecutar(conexiones, "/sql/01_esquema.sql");
        ScriptSql.ejecutar(conexiones, "/sql/02_datos.sql");
        // El próximo lunes a las 10:00: siempre es futuro y laborable
        String lunes = LocalDate.now().with(TemporalAdjusters.next(java.time.DayOfWeek.MONDAY)) + "T10:00";

        try (Servidor servidor = Servidor.crear(0, conexiones)) {
            servidor.arrancar();
            try (Socket socket = new Socket("localhost", servidor.puerto());
                 PrintWriter salida = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8);
                 BufferedReader entrada = new BufferedReader(
                         new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {

                // Pacientes de 02_datos.sql: 12345678Z activo y 87654321X de baja
                salida.println("ASIGNAR_CITA;12345678Z;1;1;" + lunes + ";");
                assertTrue(entrada.readLine().startsWith("OK;1;12345678Z;"));
                salida.println("ASIGNAR_CITA;87654321X;1;2;" + lunes + ";");
                assertEquals("ERROR;PACIENTE_DE_BAJA;dni", entrada.readLine());

                // Baja del paciente (CU12): su cita pendiente pasa a CANCELADA (DEC-048)
                salida.println("BAJA_PACIENTE;12345678Z");
                assertTrue(entrada.readLine().startsWith("OK;12345678Z;"));
                salida.println("BUSCAR_CITAS;12345678Z;;;");
                assertTrue(entrada.readLine().endsWith(";CANCELADA;Baja del paciente"));
            }
        }
    }
}
