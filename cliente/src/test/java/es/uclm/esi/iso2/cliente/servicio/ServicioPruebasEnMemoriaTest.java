package es.uclm.esi.iso2.cliente.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import es.uclm.esi.iso2.comun.dominio.EstadoPrueba;
import es.uclm.esi.iso2.comun.dominio.FiltroPruebas;
import es.uclm.esi.iso2.comun.dominio.Incidencia;
import es.uclm.esi.iso2.comun.dominio.NuevaIncidencia;
import es.uclm.esi.iso2.comun.dominio.Prueba;
import es.uclm.esi.iso2.comun.dominio.ResumenPrueba;
import es.uclm.esi.iso2.comun.dominio.SolicitudPendiente;
import es.uclm.esi.iso2.comun.dominio.TipoIncidencia;
import es.uclm.esi.iso2.comun.protocolo.CodigoError;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ServicioPruebasEnMemoriaTest {

    static final ZoneId ZONA = ZoneId.of("Europe/Madrid");
    static final LocalDateTime AHORA = LocalDateTime.of(2026, 11, 26, 10, 0);
    static final LocalDateTime HECHA = LocalDateTime.of(2026, 11, 26, 9, 30);
    static final String MEDICO = "medico1";
    static final NuevaIncidencia INCIDENCIA = new NuevaIncidencia(TipoIncidencia.TECNICA, "Fallo");

    private ServicioPruebasEnMemoria servicio;

    @BeforeEach
    void preparar() {
        servicio = new ServicioPruebasEnMemoria(Clock.fixed(AHORA.atZone(ZONA).toInstant(), ZONA));
        servicio.anadirPendiente(MEDICO, new SolicitudPendiente(1, "12345678Z", "Lucía", "Rx", "NORMAL", 1,
                LocalDateTime.of(2026, 12, 1, 11, 0)));
        servicio.anadirPendiente(MEDICO, new SolicitudPendiente(2, "X1234567L", "Ahmed", "Eco", "URGENTE", 2,
                LocalDateTime.of(2026, 12, 1, 9, 0)));
        servicio.anadirPendiente("medico2", new SolicitudPendiente(3, "12345678Z", "Lucía", "Rx", "NORMAL", 3,
                LocalDateTime.of(2026, 12, 1, 10, 0)));
    }

    private static void assertCodigo(CodigoError codigo, String detalle, Runnable accion) {
        ExcepcionServicioPruebas e = assertThrows(ExcepcionServicioPruebas.class, accion::run);
        assertEquals(codigo, e.codigo());
        assertEquals(detalle, e.detalle());
    }

    @Test
    void conDatosDeEjemploTieneSolicitudesDelMedico() {
        ServicioPruebasEnMemoria ejemplo = ServicioPruebasEnMemoria.conDatosDeEjemplo(MEDICO);
        assertEquals(3, ejemplo.listarPendientes(MEDICO, null).size());
        assertTrue(ejemplo.listarPendientes("otro", null).isEmpty());
    }

    @Test
    void pendientesOrdenadosPorCitaYFiltradosPorDni() {
        assertEquals(List.of(2L, 1L), servicio.listarPendientes(MEDICO, " ").stream()
                .map(SolicitudPendiente::idSolicitud).toList());
        assertEquals(List.of(1L), servicio.listarPendientes(MEDICO, "12345678z").stream()
                .map(SolicitudPendiente::idSolicitud).toList());
        assertCodigo(CodigoError.CAMPO_OBLIGATORIO, "idMedico", () -> servicio.listarPendientes("", null));
    }

    @Test
    void registrarRealizacionQuitaLaSolicitudDePendientes() {
        Prueba p = servicio.registrarRealizacion(MEDICO, 1, HECHA, " ok ", List.of(INCIDENCIA));
        assertEquals(EstadoPrueba.REALIZADA, p.estado());
        assertEquals("ok", p.observaciones());
        assertEquals(1, p.incidencias().size());
        assertEquals(List.of(2L), servicio.listarPendientes(MEDICO, null).stream()
                .map(SolicitudPendiente::idSolicitud).toList());
        assertEquals(p, servicio.obtener(MEDICO, p.id()));
    }

    @Test
    void registrarRealizacionConErrores() {
        assertCodigo(CodigoError.CAMPO_OBLIGATORIO, "fechaHora",
                () -> servicio.registrarRealizacion(MEDICO, 1, null, "", null));
        assertCodigo(CodigoError.FECHA_FUTURA, "fechaHora",
                () -> servicio.registrarRealizacion(MEDICO, 1, AHORA.plusDays(1), "", List.of()));
        assertCodigo(CodigoError.SOLICITUD_NO_ENCONTRADA, null,
                () -> servicio.registrarRealizacion(MEDICO, 99, HECHA, "", List.of()));
        assertCodigo(CodigoError.SOLICITUD_NO_PERMITIDA, null,
                () -> servicio.registrarRealizacion(MEDICO, 3, HECHA, "", List.of()));
    }

    @Test
    void unaSolicitudSoloTieneUnaPrueba() {
        Prueba p = servicio.registrarCancelacion(MEDICO, 1, " Ausente ");
        assertEquals(EstadoPrueba.CANCELADA, p.estado());
        assertEquals("Ausente", p.motivoCancelacion());
        assertCodigo(CodigoError.PRUEBA_YA_EXISTE, String.valueOf(p.id()),
                () -> servicio.registrarRealizacion(MEDICO, 1, HECHA, "", List.of()));
        assertCodigo(CodigoError.CAMPO_OBLIGATORIO, "motivo", () -> servicio.registrarCancelacion(MEDICO, 2, " "));
    }

    @Test
    void incidencias() {
        Prueba p = servicio.registrarRealizacion(MEDICO, 1, HECHA, "", List.of());
        Incidencia i = servicio.anadirIncidencia(MEDICO, p.id(), new NuevaIncidencia(TipoIncidencia.OTRA, " x "));
        assertEquals("x", i.descripcion());
        assertEquals(List.of(i), servicio.obtener(MEDICO, p.id()).incidencias());
        assertCodigo(CodigoError.CAMPO_OBLIGATORIO, "tipo",
                () -> servicio.anadirIncidencia(MEDICO, p.id(), new NuevaIncidencia(null, "x")));
        assertCodigo(CodigoError.PRUEBA_NO_ENCONTRADA, null, () -> servicio.anadirIncidencia(MEDICO, 99, INCIDENCIA));
        assertCodigo(CodigoError.PRUEBA_NO_PERMITIDA, null,
                () -> servicio.anadirIncidencia("medico2", p.id(), INCIDENCIA));
    }

    @Test
    void buscarYObtener() {
        Prueba a = servicio.registrarRealizacion(MEDICO, 1, HECHA, "", List.of(INCIDENCIA));
        Prueba b = servicio.registrarCancelacion(MEDICO, 2, "Ausente");
        LocalDate hoy = AHORA.toLocalDate();

        List<ResumenPrueba> todas = servicio.buscar(MEDICO, null);
        assertEquals(List.of(b.id(), a.id()), todas.stream().map(ResumenPrueba::id).toList());
        assertEquals(1, todas.get(1).numIncidencias());
        assertEquals(1, servicio.buscar(MEDICO, new FiltroPruebas("12345678z", null, hoy, hoy, null)).size());
        assertEquals(1, servicio.buscar(MEDICO, new FiltroPruebas(null, 2L, null, null, EstadoPrueba.CANCELADA))
                .size());
        assertTrue(servicio.buscar(MEDICO, new FiltroPruebas(null, null, hoy.plusDays(1), null, null)).isEmpty());
        assertTrue(servicio.buscar(MEDICO, new FiltroPruebas(null, null, null, hoy.minusDays(1), null)).isEmpty());
        assertTrue(servicio.buscar("medico2", null).isEmpty());
        assertCodigo(CodigoError.CAMPO_NO_VALIDO, "hasta",
                () -> servicio.buscar(MEDICO, new FiltroPruebas(null, null, hoy, hoy.minusDays(1), null)));
        assertCodigo(CodigoError.PRUEBA_NO_ENCONTRADA, null, () -> servicio.obtener(MEDICO, 99));
        assertCodigo(CodigoError.PRUEBA_NO_PERMITIDA, null, () -> servicio.obtener("medico2", a.id()));
        assertCodigo(CodigoError.CAMPO_OBLIGATORIO, "idMedico", () -> servicio.obtener(" ", a.id()));
    }
}
