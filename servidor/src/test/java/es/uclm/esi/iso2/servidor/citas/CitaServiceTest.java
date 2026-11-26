package es.uclm.esi.iso2.servidor.citas;

import static es.uclm.esi.iso2.servidor.citas.PacientesSimulados.ACTIVO;
import static es.uclm.esi.iso2.servidor.citas.PacientesSimulados.DE_BAJA;
import static es.uclm.esi.iso2.servidor.citas.PacientesSimulados.NO_EXISTE;
import static es.uclm.esi.iso2.servidor.citas.PacientesSimulados.OTRO_ACTIVO;
import static es.uclm.esi.iso2.servidor.citas.PacientesSimulados.RECHAZADO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import es.uclm.esi.iso2.comun.dominio.Cita;
import es.uclm.esi.iso2.comun.dominio.DatosCita;
import es.uclm.esi.iso2.comun.dominio.EstadoCita;
import es.uclm.esi.iso2.comun.protocolo.CodigoError;
import es.uclm.esi.iso2.servidor.persistencia.FabricaConexiones;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Pruebas de {@link CitaService} sobre H2 con el paciente simulado. «Ahora» es el jueves
 * 26/11/2026 a las 10:00; el martes 01/12/2026 es un día laborable futuro.
 */
class CitaServiceTest {

    private static final ZoneId ZONA = ZoneId.of("Europe/Madrid");
    private static final LocalDateTime AHORA = LocalDateTime.of(2026, 11, 26, 10, 0);
    private static final LocalDateTime MARTES_10 = LocalDateTime.of(2026, 12, 1, 10, 0);

    /** Radiografía de tórax: 15 minutos en los datos de ejemplo. */
    private static final int RADIOGRAFIA = 1;
    private static final int ECOGRAFIA = 2;
    private static final int SALA_RAYOS = 1;
    private static final int ECOGRAFO = 2;

    private FabricaConexiones conexiones;
    private CitaService servicio;

    @BeforeEach
    void preparar() {
        conexiones = BaseDatosCitas.nueva();
        servicio = servicioEn(AHORA);
    }

    private CitaService servicioEn(LocalDateTime momento) {
        Clock reloj = Clock.fixed(momento.atZone(ZONA).toInstant(), ZONA);
        return new CitaService(new CitaDAO(conexiones), new PacientesSimulados(), reloj);
    }

    private Cita asignar(String dni, int recurso, LocalDateTime inicio, Integer duracion) {
        return servicio.asignarCita(new DatosCita(dni, RADIOGRAFIA, recurso, inicio, duracion));
    }

    private static void error(CodigoError codigo, String detalle, Executable operacion) {
        CitaException e = assertThrows(CitaException.class, operacion);
        assertEquals(codigo, e.codigo());
        assertEquals(detalle, e.detalle());
    }

    @Nested
    class Asignar {

        @Test
        void creaUnaCitaProgramadaConIdYLaDuracionDelTipo() {
            Cita cita = asignar(ACTIVO, SALA_RAYOS, MARTES_10, null);
            assertTrue(cita.id() > 0);
            assertEquals(EstadoCita.PROGRAMADA, cita.estado());
            assertEquals(15, cita.duracionMinutos());
            assertEquals(cita, servicio.obtenerCita(cita.id()));
        }

        @Test
        void respetaLaDuracionIndicadaYNormalizaElDni() {
            Cita cita = asignar(" x1234567l ", SALA_RAYOS, MARTES_10, 40);
            assertEquals(40, cita.duracionMinutos());
            assertEquals(OTRO_ACTIVO, cita.dniPaciente());
        }

        @Test
        void datosObligatorios() {
            error(CodigoError.CAMPO_OBLIGATORIO, "dni",
                    () -> servicio.asignarCita(new DatosCita(" ", RADIOGRAFIA, SALA_RAYOS, MARTES_10, null)));
            error(CodigoError.CAMPO_OBLIGATORIO, "idTipoPrueba",
                    () -> servicio.asignarCita(new DatosCita(ACTIVO, null, SALA_RAYOS, MARTES_10, null)));
            error(CodigoError.CAMPO_OBLIGATORIO, "idRecurso",
                    () -> servicio.asignarCita(new DatosCita(ACTIVO, RADIOGRAFIA, null, MARTES_10, null)));
            error(CodigoError.CAMPO_OBLIGATORIO, "inicio",
                    () -> servicio.asignarCita(new DatosCita(ACTIVO, RADIOGRAFIA, SALA_RAYOS, null, null)));
        }

        @Test
        void datosNoValidos() {
            error(CodigoError.CAMPO_NO_VALIDO, "dni", () -> asignar("12345678A", SALA_RAYOS, MARTES_10, null));
            error(CodigoError.CAMPO_NO_VALIDO, "idTipoPrueba",
                    () -> servicio.asignarCita(new DatosCita(ACTIVO, 99, SALA_RAYOS, MARTES_10, null)));
            error(CodigoError.CAMPO_NO_VALIDO, "idRecurso", () -> asignar(ACTIVO, 99, MARTES_10, null));
            error(CodigoError.CAMPO_NO_VALIDO, "duracion", () -> asignar(ACTIVO, SALA_RAYOS, MARTES_10, 0));
            error(CodigoError.CAMPO_NO_VALIDO, "duracion", () -> asignar(ACTIVO, SALA_RAYOS, MARTES_10, -5));
        }

        @Test
        void elPacienteDebeExistirYEstarActivo() {
            error(CodigoError.PACIENTE_NO_ENCONTRADO, null, () -> asignar(NO_EXISTE, SALA_RAYOS, MARTES_10, null));
            error(CodigoError.PACIENTE_DE_BAJA, "dni", () -> asignar(DE_BAJA, SALA_RAYOS, MARTES_10, null));
            error(CodigoError.CAMPO_NO_VALIDO, "dni", () -> asignar(RECHAZADO, SALA_RAYOS, MARTES_10, null));
        }

        @Test
        void noSePuedeCitarEnElPasadoNiAhoraMismo() {
            error(CodigoError.FECHA_PASADA, "inicio", () -> asignar(ACTIVO, SALA_RAYOS, AHORA, null));
            error(CodigoError.FECHA_PASADA, "inicio", () -> asignar(ACTIVO, SALA_RAYOS, AHORA.minusDays(1), null));
            Cita unMinutoDespues = asignar(ACTIVO, SALA_RAYOS, AHORA.plusMinutes(1), null);
            assertEquals(AHORA.plusMinutes(1), unMinutoDespues.inicio());
        }

        /** Inicio y duración (minutos) frente al horario L-V 08:00-20:00 (DEC-044). */
        @ParameterizedTest(name = "{0} + {1} min → dentro: {2}")
        @CsvSource({
                "2026-12-01T08:00, 15, true",   // empieza justo a la apertura
                "2026-12-01T07:59, 15, false",  // un minuto antes de abrir
                "2026-12-01T19:45, 15, true",   // termina justo al cierre
                "2026-12-01T19:46, 15, false",  // termina un minuto después del cierre
                "2026-12-01T19:00, 600, false", // termina al día siguiente
                "2026-12-04T10:00, 15, true",   // viernes
                "2026-12-05T10:00, 15, false",  // sábado
                "2026-12-06T10:00, 15, false"   // domingo
        })
        void horario(LocalDateTime inicio, int duracion, boolean dentro) {
            if (dentro) {
                assertEquals(inicio, asignar(ACTIVO, SALA_RAYOS, inicio, duracion).inicio());
            } else {
                error(CodigoError.FUERA_DE_HORARIO, "inicio", () -> asignar(ACTIVO, SALA_RAYOS, inicio, duracion));
            }
        }

        @Test
        void noSeSolapaConOtraDelMismoRecurso() {
            Cita primera = asignar(ACTIVO, SALA_RAYOS, MARTES_10, 30);
            error(CodigoError.SOLAPE_RECURSO, String.valueOf(primera.id()),
                    () -> asignar(OTRO_ACTIVO, SALA_RAYOS, MARTES_10.plusMinutes(15), 30));
            // Justo al terminar sí se puede, y en otro recurso también
            asignar(OTRO_ACTIVO, SALA_RAYOS, MARTES_10.plusMinutes(30), 30);
            asignar(OTRO_ACTIVO, ECOGRAFO, MARTES_10.minusMinutes(30), 45);
        }

        @Test
        void noSeSolapaConOtraDelMismoPaciente() {
            Cita primera = asignar(ACTIVO, SALA_RAYOS, MARTES_10, 30);
            error(CodigoError.SOLAPE_PACIENTE, String.valueOf(primera.id()),
                    () -> asignar(ACTIVO, ECOGRAFO, MARTES_10.plusMinutes(29), 30));
            asignar(ACTIVO, ECOGRAFO, MARTES_10.minusMinutes(30), 30);
        }

        @Test
        void unaCitaCanceladaNoOcupaHueco() {
            Cita primera = asignar(ACTIVO, SALA_RAYOS, MARTES_10, 30);
            servicio.cancelarCita(primera.id(), "No puede venir");
            Cita nueva = asignar(OTRO_ACTIVO, SALA_RAYOS, MARTES_10, 30);
            assertNotEquals(primera.id(), nueva.id());
        }
    }

    @Nested
    class Buscar {

        private Cita a;
        private Cita b;
        private Cita c;

        @BeforeEach
        void citas() {
            c = asignar(ACTIVO, SALA_RAYOS, MARTES_10.plusDays(2), null);
            a = asignar(ACTIVO, SALA_RAYOS, MARTES_10, null);
            b = asignar(OTRO_ACTIVO, SALA_RAYOS, MARTES_10.plusDays(1), null);
            c = servicio.cancelarCita(c.id(), "Duplicada");
        }

        @Test
        void sinFiltrosDevuelveTodasOrdenadasPorInicio() {
            assertEquals(List.of(a, b, c), servicio.buscarCitas(null, null, null, null));
            assertEquals(List.of(a, b, c), servicio.buscarCitas("", null, null, null));
        }

        @Test
        void filtraPorDniFechasYEstado() {
            assertEquals(List.of(a, c), servicio.buscarCitas(" 12345678z", null, null, null));
            LocalDate martes = MARTES_10.toLocalDate();
            assertEquals(List.of(a, b), servicio.buscarCitas(null, martes, martes.plusDays(1), null));
            assertEquals(List.of(b, c), servicio.buscarCitas(null, martes.plusDays(1), null, null));
            assertEquals(List.of(a), servicio.buscarCitas(null, null, martes, null));
            assertEquals(List.of(c), servicio.buscarCitas(null, null, null, EstadoCita.CANCELADA));
            assertEquals(List.of(a), servicio.buscarCitas(ACTIVO, martes, martes, EstadoCita.PROGRAMADA));
            assertEquals(List.of(), servicio.buscarCitas(NO_EXISTE, null, null, null));
        }

        @Test
        void hastaNoPuedeSerAnteriorADesde() {
            LocalDate martes = MARTES_10.toLocalDate();
            error(CodigoError.CAMPO_NO_VALIDO, "hasta", () -> servicio.buscarCitas(null, martes, martes.minusDays(1), null));
        }
    }

    @Nested
    class Reprogramar {

        private Cita cita;

        @BeforeEach
        void cita() {
            cita = servicio.asignarCita(new DatosCita(ACTIVO, ECOGRAFIA, ECOGRAFO, MARTES_10, null));
        }

        @Test
        void cambiaLaMismaCita() {
            Cita nueva = servicio.reprogramarCita(cita.id(), SALA_RAYOS, MARTES_10.plusHours(2), 20);
            assertEquals(cita.id(), nueva.id());
            assertEquals(SALA_RAYOS, nueva.idRecurso());
            assertEquals(MARTES_10.plusHours(2), nueva.inicio());
            assertEquals(20, nueva.duracionMinutos());
            assertEquals(ECOGRAFIA, nueva.idTipoPrueba());
            assertEquals(nueva, servicio.obtenerCita(cita.id()));
            assertEquals(1, servicio.buscarCitas(null, null, null, null).size());
        }

        @Test
        void loQueNoSeIndicaNoCambia() {
            assertEquals(cita, servicio.reprogramarCita(cita.id(), null, null, null));
        }

        @Test
        void noChocaConsigoMisma() {
            Cita nueva = servicio.reprogramarCita(cita.id(), null, MARTES_10.plusMinutes(10), null);
            assertEquals(MARTES_10.plusMinutes(10), nueva.inicio());
        }

        @Test
        void siChocaConOtraNoCambiaNada() {
            Cita otra = asignar(OTRO_ACTIVO, ECOGRAFO, MARTES_10.plusHours(1), 30);
            error(CodigoError.SOLAPE_RECURSO, String.valueOf(otra.id()),
                    () -> servicio.reprogramarCita(cita.id(), null, MARTES_10.plusMinutes(45), null));
            assertEquals(cita, servicio.obtenerCita(cita.id()));
        }

        @Test
        void aplicaLasMismasReglasQueAsignar() {
            error(CodigoError.CAMPO_NO_VALIDO, "idRecurso", () -> servicio.reprogramarCita(cita.id(), 99, null, null));
            error(CodigoError.CAMPO_NO_VALIDO, "duracion", () -> servicio.reprogramarCita(cita.id(), null, null, 0));
            error(CodigoError.FECHA_PASADA, "inicio", () -> servicio.reprogramarCita(cita.id(), null, AHORA, null));
            error(CodigoError.FUERA_DE_HORARIO, "inicio",
                    () -> servicio.reprogramarCita(cita.id(), null, MARTES_10.withHour(21), null));
        }

        @Test
        void soloCitasProgramadasQueNoHanEmpezado() {
            error(CodigoError.CITA_NO_ENCONTRADA, null, () -> servicio.reprogramarCita(999, null, null, null));
            CitaService masTarde = servicioEn(MARTES_10);
            error(CodigoError.CITA_NO_MODIFICABLE, null, () -> masTarde.reprogramarCita(cita.id(), null, null, null));
            servicio.cancelarCita(cita.id(), "Error");
            error(CodigoError.CITA_NO_MODIFICABLE, null, () -> servicio.reprogramarCita(cita.id(), null, null, null));
        }
    }

    @Nested
    class Cancelar {

        private Cita cita;

        @BeforeEach
        void cita() {
            cita = asignar(ACTIVO, SALA_RAYOS, MARTES_10, null);
        }

        @Test
        void pasaACanceladaConElMotivoSinBorrarla() {
            Cita cancelada = servicio.cancelarCita(cita.id(), "  El paciente avisa; no puede  ");
            assertEquals(EstadoCita.CANCELADA, cancelada.estado());
            assertEquals("El paciente avisa; no puede", cancelada.motivoCancelacion());
            assertEquals(cancelada, servicio.obtenerCita(cita.id()));
        }

        @Test
        void motivoObligatorioYDe255ComoMaximo() {
            error(CodigoError.CAMPO_OBLIGATORIO, "motivo", () -> servicio.cancelarCita(cita.id(), null));
            error(CodigoError.CAMPO_OBLIGATORIO, "motivo", () -> servicio.cancelarCita(cita.id(), "   "));
            error(CodigoError.CAMPO_NO_VALIDO, "motivo", () -> servicio.cancelarCita(cita.id(), "x".repeat(256)));
            assertEquals(255, servicio.cancelarCita(cita.id(), "x".repeat(255)).motivoCancelacion().length());
        }

        @Test
        void soloCitasProgramadasQueNoHanEmpezado() {
            error(CodigoError.CITA_NO_ENCONTRADA, null, () -> servicio.cancelarCita(999, "Motivo"));
            error(CodigoError.CITA_NO_MODIFICABLE, null, () -> servicioEn(MARTES_10).cancelarCita(cita.id(), "Tarde"));
            servicio.cancelarCita(cita.id(), "Primera vez");
            error(CodigoError.CITA_NO_MODIFICABLE, null, () -> servicio.cancelarCita(cita.id(), "Otra vez"));
        }
    }

    @Nested
    class OperacionesDeOtrosCasosDeUso {

        private Cita cita;
        private CitaService duranteLaCita;

        @BeforeEach
        void cita() {
            cita = asignar(ACTIVO, SALA_RAYOS, MARTES_10, null);
            duranteLaCita = servicioEn(MARTES_10.plusMinutes(5));
        }

        @Test
        void obtenerCitaInexistente() {
            error(CodigoError.CITA_NO_ENCONTRADA, null, () -> servicio.obtenerCita(999));
        }

        @Test
        void marcarRealizadaAunqueHayaEmpezado() {
            assertEquals(EstadoCita.REALIZADA, duranteLaCita.marcarRealizada(cita.id()).estado());
            error(CodigoError.CITA_NO_MODIFICABLE, null, () -> duranteLaCita.marcarRealizada(cita.id()));
            error(CodigoError.CITA_NO_ENCONTRADA, null, () -> duranteLaCita.marcarRealizada(999));
        }

        @Test
        void marcarCanceladaAunqueHayaEmpezado() {
            error(CodigoError.CAMPO_OBLIGATORIO, "motivo", () -> duranteLaCita.marcarCancelada(cita.id(), ""));
            Cita cancelada = duranteLaCita.marcarCancelada(cita.id(), "No se presenta");
            assertEquals(EstadoCita.CANCELADA, cancelada.estado());
            assertEquals("No se presenta", cancelada.motivoCancelacion());
            error(CodigoError.CITA_NO_MODIFICABLE, null, () -> duranteLaCita.marcarCancelada(cita.id(), "Otra"));
        }

        @Test
        void cancelarCitasPendientesDeUnPaciente() {
            Cita otraDelMismo = asignar(ACTIVO, SALA_RAYOS, MARTES_10.plusDays(1), null);
            Cita yaCancelada = asignar(ACTIVO, SALA_RAYOS, MARTES_10.plusDays(2), null);
            servicio.cancelarCita(yaCancelada.id(), "Antes");
            Cita deOtro = asignar(OTRO_ACTIVO, SALA_RAYOS, MARTES_10.plusDays(3), null);

            assertEquals(2, servicio.cancelarCitasPendientes(ACTIVO, "Baja del paciente"));

            assertEquals("Baja del paciente", servicio.obtenerCita(cita.id()).motivoCancelacion());
            assertEquals(EstadoCita.CANCELADA, servicio.obtenerCita(otraDelMismo.id()).estado());
            assertEquals("Antes", servicio.obtenerCita(yaCancelada.id()).motivoCancelacion());
            assertEquals(EstadoCita.PROGRAMADA, servicio.obtenerCita(deOtro.id()).estado());
            assertEquals(0, servicio.cancelarCitasPendientes(ACTIVO, "Baja del paciente"));
        }

        @Test
        void cancelarCitasPendientesValidaLosDatos() {
            error(CodigoError.CAMPO_NO_VALIDO, "dni", () -> servicio.cancelarCitasPendientes("123", "Baja"));
            error(CodigoError.CAMPO_OBLIGATORIO, "motivo", () -> servicio.cancelarCitasPendientes(ACTIVO, null));
        }
    }

    @Test
    void catalogoDeEjemplo() {
        assertEquals(5, servicio.listarTiposPrueba().size());
        assertEquals(5, servicio.listarRecursos().size());
        assertEquals("Radiografía de tórax", servicio.listarTiposPrueba().stream()
                .filter(t -> t.id() == RADIOGRAFIA).findFirst().orElseThrow().nombre());
    }

    @Test
    void elConstructorSinRelojUsaLaHoraDelSistema() {
        CitaService real = new CitaService(new CitaDAO(conexiones), new PacientesSimulados());
        error(CodigoError.FECHA_PASADA, "inicio", () -> real.asignarCita(
                new DatosCita(ACTIVO, RADIOGRAFIA, SALA_RAYOS, LocalDateTime.now().minusHours(1), null)));
    }
}
