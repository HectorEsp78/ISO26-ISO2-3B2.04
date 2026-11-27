package es.uclm.esi.iso2.cliente.logica;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import es.uclm.esi.iso2.cliente.servicio.ExcepcionServicioCitas;
import es.uclm.esi.iso2.cliente.servicio.RelojAjustable;
import es.uclm.esi.iso2.cliente.servicio.ServicioCitas;
import es.uclm.esi.iso2.cliente.servicio.ServicioCitasEnMemoria;
import es.uclm.esi.iso2.comun.dominio.Cita;
import es.uclm.esi.iso2.comun.dominio.DatosCita;
import es.uclm.esi.iso2.comun.dominio.EstadoCita;
import es.uclm.esi.iso2.comun.dominio.EstadoPaciente;
import es.uclm.esi.iso2.comun.dominio.Recurso;
import es.uclm.esi.iso2.comun.dominio.TipoPrueba;
import es.uclm.esi.iso2.comun.protocolo.CodigoError;
import es.uclm.esi.iso2.comun.protocolo.ProtocoloCitas;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Pruebas de la clase de control del cliente de CU13 con el servicio en memoria. El momento
 * actual es el miércoles 25/11/2026 a las 12:00.
 */
class GestorCitasClienteTest {

    private static final String ACTIVO = "12345678Z";
    private static final String OTRO_ACTIVO = "X1234567L";
    private static final String DE_BAJA = "87654321X";
    private static final String LUNES = "30/11/2026";
    private static final TipoPrueba RADIOGRAFIA = ServicioCitasEnMemoria.TIPOS_EJEMPLO.get(0);
    private static final Recurso SALA_RX = ServicioCitasEnMemoria.RECURSOS_EJEMPLO.get(0);
    private static final Recurso ECOGRAFO = ServicioCitasEnMemoria.RECURSOS_EJEMPLO.get(1);

    private RelojAjustable reloj;
    private ServicioCitasEnMemoria servicio;
    private GestorCitasCliente gestor;

    @BeforeEach
    void preparar() {
        reloj = new RelojAjustable();
        servicio = new ServicioCitasEnMemoria(Map.of(ACTIVO, EstadoPaciente.ACTIVO,
                OTRO_ACTIVO, EstadoPaciente.ACTIVO, DE_BAJA, EstadoPaciente.BAJA), reloj);
        gestor = new GestorCitasCliente(servicio, reloj);
        assertTrue(gestor.cargarCatalogo().exito());
    }

    private static EntradaCita entrada(String dni, Recurso recurso, String hora) {
        return new EntradaCita(dni, RADIOGRAFIA, recurso, LUNES, hora, "");
    }

    private Cita asignada(String dni, Recurso recurso, String hora) {
        ResultadoCitas<Cita> r = gestor.asignar(entrada(dni, recurso, hora));
        assertTrue(r.exito(), r.mensaje());
        return r.valor();
    }

    // ---- Catálogo ----

    @Test
    void elCatalogoSeGuardaAlCargarlo() {
        GestorCitasCliente nuevo = new GestorCitasCliente(servicio);
        assertSame(Catalogo.VACIO, nuevo.catalogo());
        ResultadoCitas<Catalogo> r = nuevo.cargarCatalogo();
        assertTrue(r.exito());
        assertEquals(5, nuevo.catalogo().tipos().size());
        assertEquals(r.valor(), nuevo.catalogo());
    }

    @Test
    void sinConexionAlCargarElCatalogo() {
        GestorCitasCliente sinServidor = new GestorCitasCliente(new ServicioSinConexion(), reloj);
        ResultadoCitas<Catalogo> r = sinServidor.cargarCatalogo();
        assertFalse(r.exito());
        assertTrue(r.sinConexion());
        assertTrue(r.mensaje().startsWith("No hay conexión con el servidor"));
        assertSame(Catalogo.VACIO, sinServidor.catalogo());
    }

    // ---- Asignar ----

    @Test
    void asignarCorrecta() {
        ResultadoCitas<Cita> r = gestor.asignar(new EntradaCita(" 12345678z ", RADIOGRAFIA, SALA_RX, " 30/11/2026 ",
                " 10:30 ", " 20 "));
        assertTrue(r.exito());
        assertEquals(ACTIVO, r.valor().dniPaciente());
        assertEquals(LocalDateTime.of(2026, 11, 30, 10, 30), r.valor().inicio());
        assertEquals(20, r.valor().duracionMinutos());
        assertEquals("Cita nº 1 asignada a 12345678Z el 30/11/2026 a las 10:30 (Radiografía de tórax, Sala de rayos X 1).",
                r.mensaje());
        assertTrue(r.camposConError().isEmpty());
    }

    @Test
    void asignarSinNadaMarcaTodosLosObligatorios() {
        ResultadoCitas<Cita> r = gestor.asignar(new EntradaCita(" ", null, null, "", null, null));
        assertFalse(r.exito());
        assertEquals(List.of(ProtocoloCitas.CAMPO_DNI, ProtocoloCitas.CAMPO_TIPO_PRUEBA, ProtocoloCitas.CAMPO_RECURSO,
                GestorCitasCliente.CAMPO_FECHA, GestorCitasCliente.CAMPO_HORA), r.camposConError());
        assertEquals("Falta el campo obligatorio «DNI/NIE del paciente». Revisa también los demás campos marcados.",
                r.mensaje());
    }

    @Test
    void asignarSoloSinTipoDePrueba() {
        ResultadoCitas<Cita> r = gestor.asignar(new EntradaCita(ACTIVO, null, SALA_RX, LUNES, "10:00", ""));
        assertEquals(List.of(ProtocoloCitas.CAMPO_TIPO_PRUEBA), r.camposConError());
        assertEquals("Falta el campo obligatorio «Tipo de prueba».", r.mensaje());
    }

    @Test
    void asignarConFormatosNoValidos() {
        ResultadoCitas<Cita> r = gestor.asignar(new EntradaCita("12345678A", RADIOGRAFIA, SALA_RX, "31/11/2026",
                "25:00", "diez"));
        assertEquals(List.of(ProtocoloCitas.CAMPO_DNI, GestorCitasCliente.CAMPO_FECHA, GestorCitasCliente.CAMPO_HORA,
                ProtocoloCitas.CAMPO_DURACION), r.camposConError());
        assertTrue(r.mensaje().startsWith("El campo «DNI/NIE del paciente» no es válido"));
    }

    @Test
    void cadaCampoVacioOMalEscritoTieneSuMensaje() {
        assertEquals("Falta el campo obligatorio «Recurso».",
                gestor.asignar(new EntradaCita(ACTIVO, RADIOGRAFIA, null, LUNES, "10:00", "")).mensaje());
        assertEquals("Falta el campo obligatorio «Fecha».",
                gestor.asignar(new EntradaCita(ACTIVO, RADIOGRAFIA, SALA_RX, " ", "10:00", "")).mensaje());
        assertEquals("Falta el campo obligatorio «Hora».",
                gestor.asignar(new EntradaCita(ACTIVO, RADIOGRAFIA, SALA_RX, LUNES, "", "")).mensaje());
        assertEquals("El campo «Fecha» no es válido: debe ser una fecha dd/mm/aaaa.",
                gestor.asignar(new EntradaCita(ACTIVO, RADIOGRAFIA, SALA_RX, "2026-11-30", "10:00", "")).mensaje());
        assertEquals("El campo «Hora» no es válido: debe ser una hora hh:mm, por ejemplo 09:30.",
                gestor.asignar(new EntradaCita(ACTIVO, RADIOGRAFIA, SALA_RX, LUNES, "10", "")).mensaje());
        assertEquals("El campo «Duración» no es válido: debe ser un número de minutos mayor que 0.",
                gestor.asignar(new EntradaCita(ACTIVO, RADIOGRAFIA, SALA_RX, LUNES, "10:00", "0")).mensaje());
    }

    @Test
    void erroresDelServidorSeMarcanEnElFormulario() {
        ResultadoCitas<Cita> pasada = gestor.asignar(new EntradaCita(ACTIVO, RADIOGRAFIA, SALA_RX, "20/11/2026",
                "10:00", ""));
        assertEquals("La cita debe empezar después del momento actual.", pasada.mensaje());
        assertEquals(List.of(GestorCitasCliente.CAMPO_FECHA, GestorCitasCliente.CAMPO_HORA), pasada.camposConError());

        ResultadoCitas<Cita> baja = gestor.asignar(entrada(DE_BAJA, SALA_RX, "10:00"));
        assertEquals("El paciente está dado de baja: no se le pueden asignar citas.", baja.mensaje());
        assertEquals(List.of(ProtocoloCitas.CAMPO_DNI), baja.camposConError());

        ResultadoCitas<Cita> noExiste = gestor.asignar(entrada("00000000T", SALA_RX, "10:00"));
        assertTrue(noExiste.mensaje().startsWith("No existe ningún paciente con ese DNI/NIE."));
    }

    @Test
    void unSolapeIndicaConQueCitaChoca() {
        Cita primera = asignada(ACTIVO, SALA_RX, "10:00");
        ResultadoCitas<Cita> r = gestor.asignar(entrada(OTRO_ACTIVO, SALA_RX, "10:05"));
        assertEquals("El recurso ya tiene otra cita a esa hora (choca con la cita nº " + primera.id() + ").",
                r.mensaje());
        assertEquals(List.of(ProtocoloCitas.CAMPO_RECURSO, GestorCitasCliente.CAMPO_FECHA,
                GestorCitasCliente.CAMPO_HORA), r.camposConError());

        ResultadoCitas<Cita> paciente = gestor.asignar(entrada(ACTIVO, ECOGRAFO, "10:05"));
        assertEquals("El paciente ya tiene otra cita a esa hora (choca con la cita nº " + primera.id() + ").",
                paciente.mensaje());
    }

    @Test
    void sinConexionAlAsignar() {
        GestorCitasCliente sinServidor = new GestorCitasCliente(new ServicioSinConexion(), reloj);
        ResultadoCitas<Cita> r = sinServidor.asignar(entrada(ACTIVO, SALA_RX, "10:00"));
        assertTrue(r.sinConexion());
        assertTrue(r.camposConError().isEmpty());
    }

    // ---- Reprogramar ----

    @Test
    void reprogramarCorrecta() {
        Cita cita = asignada(ACTIVO, SALA_RX, "10:00");
        ResultadoCitas<Cita> r = gestor.reprogramar(cita,
                new EntradaCita("se ignora", null, ECOGRAFO, "01/12/2026", "11:00", "30"));
        assertTrue(r.exito(), r.mensaje());
        assertEquals(cita.id(), r.valor().id());
        assertEquals(ACTIVO, r.valor().dniPaciente());
        assertEquals("Cita nº 1 reprogramada al 01/12/2026 a las 11:00 (Ecógrafo 1).", r.mensaje());
    }

    @Test
    void reprogramarConErrores() {
        Cita cita = asignada(ACTIVO, SALA_RX, "10:00");
        ResultadoCitas<Cita> vacio = gestor.reprogramar(cita, new EntradaCita(null, null, null, "", "", ""));
        assertEquals(List.of(ProtocoloCitas.CAMPO_RECURSO, GestorCitasCliente.CAMPO_FECHA,
                GestorCitasCliente.CAMPO_HORA), vacio.camposConError());

        ResultadoCitas<Cita> fuera = gestor.reprogramar(cita, entrada(ACTIVO, SALA_RX, "20:30"));
        assertEquals("La cita debe empezar y terminar de lunes a viernes, entre las 08:00 y las 20:00.",
                fuera.mensaje());
    }

    @Test
    void noSeReprogramaNiCancelaUnaCitaQueYaNoEsModificable() {
        Cita cita = asignada(ACTIVO, SALA_RX, "10:00");
        reloj.poner(LocalDateTime.of(2026, 11, 30, 10, 1));
        String esperado = "La cita ya no se puede modificar: está cancelada o realizada, o ya ha empezado.";
        assertEquals(esperado, gestor.reprogramar(cita, entrada(ACTIVO, SALA_RX, "12:00")).mensaje());
        assertEquals(esperado, gestor.cancelar(cita, "Motivo").mensaje());
    }

    // ---- Cancelar ----

    @Test
    void cancelarCorrecta() {
        Cita cita = asignada(ACTIVO, SALA_RX, "10:00");
        ResultadoCitas<Cita> r = gestor.cancelar(cita, " No puede venir ");
        assertTrue(r.exito());
        assertEquals(EstadoCita.CANCELADA, r.valor().estado());
        assertEquals("No puede venir", r.valor().motivoCancelacion());
        assertEquals("Cita nº 1 cancelada.", r.mensaje());
    }

    @Test
    void cancelarSinMotivoOConMotivoLargo() {
        Cita cita = asignada(ACTIVO, SALA_RX, "10:00");
        ResultadoCitas<Cita> sinMotivo = gestor.cancelar(cita, null);
        assertEquals("Falta el campo obligatorio «Motivo de la cancelación».", sinMotivo.mensaje());
        assertEquals(List.of(ProtocoloCitas.CAMPO_MOTIVO), sinMotivo.camposConError());
        ResultadoCitas<Cita> largo = gestor.cancelar(cita, "x".repeat(256));
        assertEquals("El campo «Motivo de la cancelación» no es válido: debe ser un texto de hasta 255 caracteres.",
                largo.mensaje());
    }

    @Test
    void cancelarUnaCitaQueOtroYaHaCancelado() {
        Cita cita = asignada(ACTIVO, SALA_RX, "10:00");
        servicio.cancelar(cita.id(), "Cancelada desde otro puesto");
        ResultadoCitas<Cita> r = gestor.cancelar(cita, "Motivo");
        assertFalse(r.exito());
        assertTrue(r.mensaje().startsWith("La cita ya no se puede modificar"));
    }

    // ---- Buscar ----

    @Test
    void buscarCuentaLasCitas() {
        assertEquals("No hay ninguna cita con esos filtros.", gestor.buscar("", "", "", null).mensaje());
        asignada(ACTIVO, SALA_RX, "10:00");
        assertEquals("Se ha encontrado 1 cita.", gestor.buscar(null, null, null, null).mensaje());
        asignada(OTRO_ACTIVO, ECOGRAFO, "10:00");
        ResultadoCitas<List<Cita>> todas = gestor.buscar(" ", LUNES, LUNES, EstadoCita.PROGRAMADA);
        assertEquals("Se han encontrado 2 citas.", todas.mensaje());
        assertEquals(2, todas.valor().size());
        assertEquals(1, gestor.buscar("x1234567l", null, null, null).valor().size());
    }

    @Test
    void buscarConFiltrosNoValidos() {
        ResultadoCitas<List<Cita>> r = gestor.buscar("123", "32/11/2026", "", null);
        assertEquals(List.of(ProtocoloCitas.CAMPO_DNI, ProtocoloCitas.CAMPO_DESDE), r.camposConError());
        assertTrue(r.mensaje().startsWith("El campo «DNI/NIE del paciente» no es válido"));

        ResultadoCitas<List<Cita>> alReves = gestor.buscar("", "30/11/2026", "29/11/2026", null);
        assertEquals(List.of(ProtocoloCitas.CAMPO_HASTA), alReves.camposConError());
        assertEquals("El campo «Hasta» no es válido: debe ser una fecha dd/mm/aaaa que no sea anterior a «Desde».",
                alReves.mensaje());
    }

    // ---- Otros ----

    @Test
    void soloSeModificanLasProgramadasQueNoHanEmpezado() {
        LocalDateTime futuro = LocalDateTime.of(2026, 11, 30, 10, 0);
        Cita programada = new Cita(1, ACTIVO, 1, 1, futuro, 15, EstadoCita.PROGRAMADA, "");
        assertTrue(gestor.puedeModificar(programada));
        assertFalse(gestor.puedeModificar(null));
        assertFalse(gestor.puedeModificar(programada.conEstado(EstadoCita.CANCELADA, "Motivo")));
        assertFalse(gestor.puedeModificar(programada.conEstado(EstadoCita.REALIZADA, null)));
        assertFalse(gestor.puedeModificar(new Cita(1, ACTIVO, 1, 1, RelojAjustable.AHORA, 15,
                EstadoCita.PROGRAMADA, "")));
    }

    @Test
    void preguntaDeConfirmacion() {
        Cita cita = asignada(ACTIVO, SALA_RX, "10:00");
        String pregunta = gestor.preguntaConfirmacionCancelar(cita);
        assertTrue(pregunta.startsWith("¿Seguro que quieres cancelar la cita nº 1 de 12345678Z (Radiografía de tórax)"
                + " del 30/11/2026 a las 10:00?"));
    }

    @Test
    void camposAfectadosPorCadaError() {
        assertEquals(List.of(GestorCitasCliente.CAMPO_FECHA, GestorCitasCliente.CAMPO_HORA),
                GestorCitasCliente.camposAfectados(CodigoError.CAMPO_OBLIGATORIO, ProtocoloCitas.CAMPO_INICIO));
        assertEquals(List.of(ProtocoloCitas.CAMPO_DURACION),
                GestorCitasCliente.camposAfectados(CodigoError.CAMPO_NO_VALIDO, ProtocoloCitas.CAMPO_DURACION));
        assertEquals(List.of(), GestorCitasCliente.camposAfectados(CodigoError.CAMPO_NO_VALIDO, null));
        assertEquals(List.of(), GestorCitasCliente.camposAfectados(CodigoError.ERROR_INTERNO, null));
        assertEquals(List.of(ProtocoloCitas.CAMPO_DNI),
                GestorCitasCliente.camposAfectados(CodigoError.PACIENTE_NO_ENCONTRADO, null));
    }

    /** Servicio que simula que el servidor está apagado. */
    private static final class ServicioSinConexion implements ServicioCitas {

        private static ExcepcionServicioCitas fallo() {
            return new ExcepcionServicioCitas(CodigoError.SIN_CONEXION);
        }

        @Override
        public Cita asignar(DatosCita datos) {
            throw fallo();
        }

        @Override
        public List<Cita> buscar(String dni, LocalDate desde, LocalDate hasta, EstadoCita estado) {
            throw fallo();
        }

        @Override
        public Cita reprogramar(long id, Integer idRecurso, LocalDateTime inicio, Integer duracionMinutos) {
            throw fallo();
        }

        @Override
        public Cita cancelar(long id, String motivo) {
            throw fallo();
        }

        @Override
        public List<TipoPrueba> listarTiposPrueba() {
            throw fallo();
        }

        @Override
        public List<Recurso> listarRecursos() {
            throw fallo();
        }
    }

    @Test
    void elValorDeUnErrorEsNulo() {
        assertNull(ResultadoCitas.error("x", List.of()).valor());
    }
}
