package es.uclm.esi.iso2.servidor.red;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import es.uclm.esi.iso2.comun.dominio.EstadoPaciente;
import es.uclm.esi.iso2.comun.protocolo.CodigoError;
import es.uclm.esi.iso2.comun.protocolo.Protocolo;
import es.uclm.esi.iso2.servidor.citas.CitaDAO;
import es.uclm.esi.iso2.servidor.citas.CitaService;
import es.uclm.esi.iso2.servidor.pacientes.PacienteException;
import es.uclm.esi.iso2.servidor.persistencia.FabricaConexiones;
import es.uclm.esi.iso2.servidor.persistencia.ScriptSql;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Pruebas de las operaciones de CU13 a nivel de protocolo: de la línea recibida a la línea de
 * respuesta, con H2 y un paciente activo simulado. «Ahora» es el 26/11/2026 a las 10:00.
 */
class OperacionesCitasTest {

    private static final ZoneId ZONA = ZoneId.of("Europe/Madrid");
    private static int contador;

    private ManejadorPeticiones manejador;

    @BeforeEach
    void preparar() {
        FabricaConexiones conexiones = FabricaConexiones.jdbc(
                "jdbc:h2:mem:opcitas" + (++contador) + ";MODE=MySQL;DB_CLOSE_DELAY=-1", "sa", "");
        ScriptSql.ejecutar(conexiones, "/sql/01_esquema.sql");
        ScriptSql.ejecutar(conexiones, "/sql/02_datos.sql");
        Clock reloj = Clock.fixed(LocalDateTime.of(2026, 11, 26, 10, 0).atZone(ZONA).toInstant(), ZONA);
        CitaService servicio = new CitaService(new CitaDAO(conexiones), dni -> {
            if (!"12345678Z".equals(dni)) {
                throw new PacienteException(CodigoError.PACIENTE_NO_ENCONTRADO);
            }
            return EstadoPaciente.ACTIVO;
        }, reloj);
        manejador = new ManejadorPeticiones();
        OperacionesCitas.registrar(manejador, servicio);
    }

    private String asignar(String inicio) {
        return manejador.atender("ASIGNAR_CITA;12345678Z;1;1;" + inicio + ";");
    }

    @Test
    void asignarDevuelveLaCitaCompleta() {
        assertEquals("OK;1;12345678Z;1;1;2026-12-01T10:00;15;2026-12-01T10:15;PROGRAMADA;",
                asignar("2026-12-01T10:00"));
    }

    @Test
    void asignarConErroresDelServicio() {
        asignar("2026-12-01T10:00");
        assertEquals("ERROR;SOLAPE_RECURSO;1", asignar("2026-12-01T10:05"));
        assertEquals("ERROR;CAMPO_OBLIGATORIO;inicio", asignar(""));
        assertEquals("ERROR;PACIENTE_NO_ENCONTRADO",
                manejador.atender("ASIGNAR_CITA;11111111H;1;1;2026-12-02T10:00;"));
    }

    @Test
    void camposConFormatoIncorrecto() {
        assertEquals("ERROR;CAMPO_NO_VALIDO;idTipoPrueba",
                manejador.atender("ASIGNAR_CITA;12345678Z;uno;1;2026-12-01T10:00;"));
        assertEquals("ERROR;CAMPO_NO_VALIDO;inicio", asignar("01/12/2026 10:00"));
        assertEquals("ERROR;CAMPO_NO_VALIDO;duracion",
                manejador.atender("ASIGNAR_CITA;12345678Z;1;1;2026-12-01T10:00;media hora"));
        assertEquals("ERROR;CAMPO_NO_VALIDO;desde", manejador.atender("BUSCAR_CITAS;;ayer;;"));
        assertEquals("ERROR;CAMPO_NO_VALIDO;estado", manejador.atender("BUSCAR_CITAS;;;;PENDIENTE"));
        assertEquals("ERROR;CAMPO_NO_VALIDO;id", manejador.atender("CANCELAR_CITA;abc;Motivo"));
        assertEquals("ERROR;CAMPO_OBLIGATORIO;id", manejador.atender("CANCELAR_CITA; ;Motivo"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "ASIGNAR_CITA;12345678Z;1;1;2026-12-01T10:00",
            "BUSCAR_CITAS;;;",
            "REPROGRAMAR_CITA;1;;",
            "CANCELAR_CITA;1",
            "LISTAR_TIPOS_PRUEBA;x",
            "LISTAR_RECURSOS;x"
    })
    void numeroDeCamposIncorrecto(String peticion) {
        assertEquals(Protocolo.respuestaError(CodigoError.PETICION_NO_VALIDA, null), manejador.atender(peticion));
    }

    @Test
    void buscarReprogramarYCancelar() {
        asignar("2026-12-01T10:00");
        asignar("2026-12-02T10:00");
        assertTrue(manejador.atender("BUSCAR_CITAS;12345678Z;2026-12-01;2026-12-31;PROGRAMADA").startsWith("OK;2;1;"));
        assertEquals("OK;0", manejador.atender("BUSCAR_CITAS;;2026-12-03;;"));

        assertEquals("OK;1;12345678Z;1;2;2026-12-01T12:00;20;2026-12-01T12:20;PROGRAMADA;",
                manejador.atender("REPROGRAMAR_CITA;1;2;2026-12-01T12:00;20"));
        assertEquals("ERROR;CITA_NO_ENCONTRADA", manejador.atender("REPROGRAMAR_CITA;9;;;"));

        assertEquals("OK;2;12345678Z;1;1;2026-12-02T10:00;15;2026-12-02T10:15;CANCELADA;No puede\\; avisa",
                manejador.atender("CANCELAR_CITA;2;No puede\\; avisa"));
        assertEquals("ERROR;CITA_NO_MODIFICABLE", manejador.atender("CANCELAR_CITA;2;Otra vez"));
    }

    @Test
    void catalogo() {
        assertTrue(manejador.atender("LISTAR_TIPOS_PRUEBA").startsWith("OK;5;"));
        assertTrue(manejador.atender("LISTAR_RECURSOS").startsWith("OK;5;"));
    }
}
