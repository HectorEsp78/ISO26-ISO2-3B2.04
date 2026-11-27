package es.uclm.esi.iso2.cliente.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import es.uclm.esi.iso2.comun.dominio.Cita;
import es.uclm.esi.iso2.comun.dominio.DatosCita;
import es.uclm.esi.iso2.comun.dominio.EstadoCita;
import es.uclm.esi.iso2.comun.dominio.EstadoPaciente;
import es.uclm.esi.iso2.comun.protocolo.CodigoError;
import es.uclm.esi.iso2.comun.protocolo.ProtocoloCitas;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

/**
 * Pruebas del servicio de citas en memoria: debe aplicar las mismas reglas que el servidor.
 * El momento actual es el miércoles 25/11/2026 a las 12:00.
 */
class ServicioCitasEnMemoriaTest {

    static final String ACTIVO = "12345678Z";
    static final String OTRO_ACTIVO = "X1234567L";
    static final String DE_BAJA = "87654321X";
    static final String NO_EXISTE = "00000000T";
    /** Lunes 30/11/2026. */
    static final LocalDate LUNES = LocalDate.of(2026, 11, 30);

    private RelojAjustable reloj;
    private ServicioCitasEnMemoria servicio;

    @BeforeEach
    void preparar() {
        reloj = new RelojAjustable();
        servicio = new ServicioCitasEnMemoria(Map.of(ACTIVO, EstadoPaciente.ACTIVO,
                OTRO_ACTIVO, EstadoPaciente.ACTIVO, DE_BAJA, EstadoPaciente.BAJA), reloj);
    }

    private static DatosCita datos(String dni, int recurso, int hora, int minuto, Integer duracion) {
        return new DatosCita(dni, 1, recurso, LUNES.atTime(hora, minuto), duracion);
    }

    private static void assertError(CodigoError codigo, String detalle, Executable operacion) {
        ExcepcionServicioCitas e = assertThrows(ExcepcionServicioCitas.class, operacion);
        assertEquals(codigo, e.codigo());
        assertEquals(detalle, e.detalle());
    }

    // ---- Asignar ----

    @Test
    void asignarCitaCorrectaConDuracionDelTipo() {
        Cita cita = servicio.asignar(datos(" 12345678z ", 1, 10, 0, null));
        assertEquals(1, cita.id());
        assertEquals(ACTIVO, cita.dniPaciente());
        assertEquals(15, cita.duracionMinutos());
        assertEquals(EstadoCita.PROGRAMADA, cita.estado());
        assertEquals(2, servicio.asignar(datos(ACTIVO, 1, 11, 0, 20)).id());
    }

    @Test
    void asignarSinDatosObligatorios() {
        assertError(CodigoError.CAMPO_OBLIGATORIO, ProtocoloCitas.CAMPO_DNI,
                () -> servicio.asignar(new DatosCita(" ", 1, 1, LUNES.atTime(10, 0), null)));
        assertError(CodigoError.CAMPO_OBLIGATORIO, ProtocoloCitas.CAMPO_TIPO_PRUEBA,
                () -> servicio.asignar(new DatosCita(ACTIVO, null, 1, LUNES.atTime(10, 0), null)));
        assertError(CodigoError.CAMPO_OBLIGATORIO, ProtocoloCitas.CAMPO_RECURSO,
                () -> servicio.asignar(new DatosCita(ACTIVO, 1, null, LUNES.atTime(10, 0), null)));
        assertError(CodigoError.CAMPO_OBLIGATORIO, ProtocoloCitas.CAMPO_INICIO,
                () -> servicio.asignar(new DatosCita(ACTIVO, 1, 1, null, null)));
    }

    @Test
    void asignarConDatosNoValidos() {
        assertError(CodigoError.CAMPO_NO_VALIDO, ProtocoloCitas.CAMPO_DNI,
                () -> servicio.asignar(datos("12345678A", 1, 10, 0, null)));
        assertError(CodigoError.CAMPO_NO_VALIDO, ProtocoloCitas.CAMPO_TIPO_PRUEBA,
                () -> servicio.asignar(new DatosCita(ACTIVO, 99, 1, LUNES.atTime(10, 0), null)));
        assertError(CodigoError.CAMPO_NO_VALIDO, ProtocoloCitas.CAMPO_RECURSO,
                () -> servicio.asignar(datos(ACTIVO, 99, 10, 0, null)));
        assertError(CodigoError.CAMPO_NO_VALIDO, ProtocoloCitas.CAMPO_DURACION,
                () -> servicio.asignar(datos(ACTIVO, 1, 10, 0, 0)));
    }

    @Test
    void asignarAPacienteQueNoExisteODeBaja() {
        assertError(CodigoError.PACIENTE_NO_ENCONTRADO, null,
                () -> servicio.asignar(datos(NO_EXISTE, 1, 10, 0, null)));
        assertError(CodigoError.PACIENTE_DE_BAJA, ProtocoloCitas.CAMPO_DNI,
                () -> servicio.asignar(datos(DE_BAJA, 1, 10, 0, null)));
    }

    @Test
    void asignarEnElPasadoOFueraDeHorario() {
        assertError(CodigoError.FECHA_PASADA, ProtocoloCitas.CAMPO_INICIO,
                () -> servicio.asignar(new DatosCita(ACTIVO, 1, 1, RelojAjustable.AHORA, null)));
        // sábado
        assertError(CodigoError.FUERA_DE_HORARIO, ProtocoloCitas.CAMPO_INICIO,
                () -> servicio.asignar(new DatosCita(ACTIVO, 1, 1, LocalDateTime.of(2026, 11, 28, 10, 0), null)));
        // domingo
        assertError(CodigoError.FUERA_DE_HORARIO, ProtocoloCitas.CAMPO_INICIO,
                () -> servicio.asignar(new DatosCita(ACTIVO, 1, 1, LocalDateTime.of(2026, 11, 29, 10, 0), null)));
        assertError(CodigoError.FUERA_DE_HORARIO, ProtocoloCitas.CAMPO_INICIO,
                () -> servicio.asignar(datos(ACTIVO, 1, 7, 59, null)));
        assertError(CodigoError.FUERA_DE_HORARIO, ProtocoloCitas.CAMPO_INICIO,
                () -> servicio.asignar(datos(ACTIVO, 1, 19, 50, null)));
        // termina al día siguiente
        assertError(CodigoError.FUERA_DE_HORARIO, ProtocoloCitas.CAMPO_INICIO,
                () -> servicio.asignar(datos(ACTIVO, 1, 19, 0, 24 * 60)));
        // en los bordes sí se puede
        servicio.asignar(datos(ACTIVO, 1, 8, 0, null));
        servicio.asignar(datos(ACTIVO, 2, 19, 45, null));
    }

    @Test
    void solapesConElRecursoYConElPaciente() {
        Cita primera = servicio.asignar(datos(ACTIVO, 1, 10, 0, 30));
        assertError(CodigoError.SOLAPE_RECURSO, String.valueOf(primera.id()),
                () -> servicio.asignar(datos(OTRO_ACTIVO, 1, 10, 15, null)));
        assertError(CodigoError.SOLAPE_PACIENTE, String.valueOf(primera.id()),
                () -> servicio.asignar(datos(ACTIVO, 2, 10, 15, null)));
        // justo cuando termina la otra no hay solape (DEC-043)
        servicio.asignar(datos(OTRO_ACTIVO, 1, 10, 30, null));
    }

    @Test
    void lasCitasCanceladasNoOcupanHueco() {
        Cita primera = servicio.asignar(datos(ACTIVO, 1, 10, 0, 30));
        servicio.cancelar(primera.id(), "Motivo");
        Cita otra = servicio.asignar(datos(OTRO_ACTIVO, 1, 10, 0, 30));
        assertEquals(2, otra.id());
    }

    // ---- Buscar ----

    @Test
    void buscarConFiltros() {
        Cita c1 = servicio.asignar(datos(ACTIVO, 1, 12, 0, null));
        Cita c2 = servicio.asignar(datos(OTRO_ACTIVO, 2, 9, 0, null));
        Cita c3 = servicio.asignar(new DatosCita(ACTIVO, 1, 1, LUNES.plusDays(2).atTime(9, 0), null));
        servicio.cancelar(c3.id(), "No puede venir");

        assertEquals(List.of(c2.id(), c1.id(), c3.id()), ids(servicio.buscar(null, null, null, null)));
        assertEquals(List.of(c1.id(), c3.id()), ids(servicio.buscar(" 12345678z", null, null, null)));
        assertEquals(List.of(c2.id(), c1.id()), ids(servicio.buscar("", LUNES, LUNES, null)));
        assertEquals(List.of(c3.id()), ids(servicio.buscar(null, LUNES.plusDays(1), null, null)));
        assertEquals(List.of(c2.id(), c1.id()), ids(servicio.buscar(null, null, LUNES.plusDays(1), null)));
        assertEquals(List.of(c3.id()), ids(servicio.buscar(null, null, null, EstadoCita.CANCELADA)));
        assertTrue(servicio.buscar(OTRO_ACTIVO, null, null, EstadoCita.REALIZADA).isEmpty());
    }

    @Test
    void buscarConHastaAnteriorADesde() {
        assertError(CodigoError.CAMPO_NO_VALIDO, ProtocoloCitas.CAMPO_HASTA,
                () -> servicio.buscar(null, LUNES, LUNES.minusDays(1), null));
    }

    private static List<Long> ids(List<Cita> citas) {
        return citas.stream().map(Cita::id).toList();
    }

    // ---- Reprogramar ----

    @Test
    void reprogramarConservaElIdYPuedeSolaparseConsigoMisma() {
        Cita cita = servicio.asignar(datos(ACTIVO, 1, 10, 0, 30));
        Cita movida = servicio.reprogramar(cita.id(), 2, LUNES.atTime(10, 15), 45);
        assertEquals(cita.id(), movida.id());
        assertEquals(2, movida.idRecurso());
        assertEquals(LUNES.atTime(10, 15), movida.inicio());
        assertEquals(45, movida.duracionMinutos());
        assertEquals(movida, servicio.buscar(null, null, null, null).get(0));
    }

    @Test
    void reprogramarSinIndicarDatosLosDejaIgual() {
        Cita cita = servicio.asignar(datos(ACTIVO, 1, 10, 0, 30));
        assertEquals(cita, servicio.reprogramar(cita.id(), null, null, null));
    }

    @Test
    void reprogramarConErrores() {
        Cita cita = servicio.asignar(datos(ACTIVO, 1, 10, 0, 30));
        Cita otra = servicio.asignar(datos(OTRO_ACTIVO, 2, 11, 0, 30));
        assertError(CodigoError.CITA_NO_ENCONTRADA, null, () -> servicio.reprogramar(99, null, null, null));
        assertError(CodigoError.CAMPO_NO_VALIDO, ProtocoloCitas.CAMPO_RECURSO,
                () -> servicio.reprogramar(cita.id(), 99, null, null));
        assertError(CodigoError.SOLAPE_RECURSO, String.valueOf(otra.id()),
                () -> servicio.reprogramar(cita.id(), 2, LUNES.atTime(11, 0), null));
        assertError(CodigoError.FUERA_DE_HORARIO, ProtocoloCitas.CAMPO_INICIO,
                () -> servicio.reprogramar(cita.id(), null, LUNES.atTime(21, 0), null));
    }

    @Test
    void noSeReprogramanCitasCanceladasNiEmpezadas() {
        Cita cancelada = servicio.asignar(datos(ACTIVO, 1, 10, 0, 30));
        servicio.cancelar(cancelada.id(), "Motivo");
        assertError(CodigoError.CITA_NO_MODIFICABLE, null,
                () -> servicio.reprogramar(cancelada.id(), null, LUNES.atTime(12, 0), null));

        Cita empezada = servicio.asignar(datos(ACTIVO, 1, 11, 0, 30));
        reloj.poner(LUNES.atTime(11, 5));
        assertError(CodigoError.CITA_NO_MODIFICABLE, null,
                () -> servicio.reprogramar(empezada.id(), null, LUNES.atTime(12, 0), null));
        assertError(CodigoError.CITA_NO_MODIFICABLE, null, () -> servicio.cancelar(empezada.id(), "Motivo"));
    }

    // ---- Cancelar ----

    @Test
    void cancelarGuardaElMotivoSinEspacios() {
        Cita cita = servicio.asignar(datos(ACTIVO, 1, 10, 0, 30));
        Cita cancelada = servicio.cancelar(cita.id(), "  Se encuentra mal; llamará otro día  ");
        assertEquals(EstadoCita.CANCELADA, cancelada.estado());
        assertEquals("Se encuentra mal; llamará otro día", cancelada.motivoCancelacion());
    }

    @Test
    void cancelarSinMotivoOConMotivoDemasiadoLargo() {
        Cita cita = servicio.asignar(datos(ACTIVO, 1, 10, 0, 30));
        assertError(CodigoError.CAMPO_OBLIGATORIO, ProtocoloCitas.CAMPO_MOTIVO, () -> servicio.cancelar(cita.id(), null));
        assertError(CodigoError.CAMPO_OBLIGATORIO, ProtocoloCitas.CAMPO_MOTIVO, () -> servicio.cancelar(cita.id(), "  "));
        assertError(CodigoError.CAMPO_NO_VALIDO, ProtocoloCitas.CAMPO_MOTIVO,
                () -> servicio.cancelar(cita.id(), "x".repeat(256)));
        servicio.cancelar(cita.id(), "x".repeat(255));
        assertError(CodigoError.CITA_NO_MODIFICABLE, null, () -> servicio.cancelar(cita.id(), "Otra vez"));
        assertError(CodigoError.CITA_NO_ENCONTRADA, null, () -> servicio.cancelar(99, "Motivo"));
    }

    // ---- Catálogo y datos de ejemplo ----

    @Test
    void catalogoDeEjemplo() {
        assertEquals(5, servicio.listarTiposPrueba().size());
        assertEquals(5, servicio.listarRecursos().size());
        assertEquals("Radiografía de tórax", servicio.listarTiposPrueba().get(0).nombre());
    }

    @Test
    void datosDeEjemploConUnaCitaCancelada() {
        ServicioCitasEnMemoria ejemplo = ServicioCitasEnMemoria.conDatosDeEjemplo(new RelojAjustable());
        List<Cita> citas = ejemplo.buscar(null, null, null, null);
        assertEquals(3, citas.size());
        assertEquals(1, ejemplo.buscar(null, null, null, EstadoCita.CANCELADA).size());
        assertFalse(ServicioCitasEnMemoria.conDatosDeEjemplo().buscar(null, null, null, null).isEmpty());
    }

    @Test
    void siguienteDiaLaborableSaltaElFinDeSemana() {
        assertEquals(LocalDate.of(2026, 11, 26), ServicioCitasEnMemoria.siguienteDiaLaborable(LocalDate.of(2026, 11, 25)));
        assertEquals(LUNES, ServicioCitasEnMemoria.siguienteDiaLaborable(LocalDate.of(2026, 11, 27)));
        assertEquals(LUNES, ServicioCitasEnMemoria.siguienteDiaLaborable(LocalDate.of(2026, 11, 28)));
    }
}
