package es.uclm.esi.iso2.cliente.logica;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import es.uclm.esi.iso2.cliente.servicio.ExcepcionServicioPruebas;
import es.uclm.esi.iso2.cliente.servicio.ServicioPruebasEnMemoria;
import es.uclm.esi.iso2.cliente.servicio.ServicioPruebas;
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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GestorPruebasClienteTest {

    private static final ZoneId ZONA = ZoneId.of("Europe/Madrid");
    private static final LocalDateTime AHORA = LocalDateTime.of(2026, 11, 26, 10, 0);
    private static final Clock RELOJ = Clock.fixed(AHORA.atZone(ZONA).toInstant(), ZONA);
    private static final String MEDICO = "medico1";

    private ServicioPruebasEnMemoria servicio;
    private GestorPruebasCliente gestor;

    @BeforeEach
    void preparar() {
        servicio = new ServicioPruebasEnMemoria(RELOJ);
        servicio.anadirPendiente(MEDICO, new SolicitudPendiente(1, "12345678Z", "Lucía", "Rx", "NORMAL", 1,
                LocalDateTime.of(2026, 12, 1, 11, 0)));
        servicio.anadirPendiente(MEDICO, new SolicitudPendiente(2, "X1234567L", "Ahmed", "Eco", "URGENTE", 2,
                LocalDateTime.of(2026, 12, 1, 9, 0)));
        gestor = new GestorPruebasCliente(servicio, MEDICO, RELOJ);
    }

    private static void assertError(ResultadoPruebas<?> r, String campo, String mensajeContiene) {
        assertFalse(r.exito());
        assertEquals(campo, r.campoConError());
        assertTrue(r.mensaje().contains(mensajeContiene), r.mensaje());
    }

    @Test
    void listarPendientes() {
        assertEquals(MEDICO, gestor.idMedico());
        assertEquals(2, gestor.listarPendientes("").valor().size());
        assertEquals(1, gestor.listarPendientes("12345678z").valor().size());
        assertError(gestor.listarPendientes("123"), "dni", "DNI");
    }

    @Test
    void registrarRealizacionCorrecta() {
        ResultadoPruebas<Prueba> r = gestor.registrarRealizacion(1, "26/11/2026 09:30", " ok ",
                List.of(new NuevaIncidencia(TipoIncidencia.TECNICA, "Fallo")));
        assertTrue(r.exito());
        assertEquals("Prueba registrada como realizada.", r.mensaje());
        assertEquals(EstadoPrueba.REALIZADA, r.valor().estado());
        assertEquals("ok", r.valor().observaciones());
        assertEquals(LocalDateTime.of(2026, 11, 26, 9, 30), r.valor().fechaHoraRealizacion());
        assertTrue(r.campo().isEmpty());
    }

    @Test
    void registrarRealizacionSinIncidenciasNiObservaciones() {
        assertTrue(gestor.registrarRealizacion(1, "26/11/2026 09:30", null, null).exito());
    }

    @Test
    void registrarRealizacionConErroresDeValidacion() {
        assertError(gestor.registrarRealizacion(1, "", "", List.of()), "fechaHora", "Falta el campo obligatorio");
        assertError(gestor.registrarRealizacion(1, null, "", List.of()), "fechaHora", "Falta el campo obligatorio");
        assertError(gestor.registrarRealizacion(1, "ayer", "", List.of()), "fechaHora", "dd/mm/aaaa hh:mm");
        assertError(gestor.registrarRealizacion(1, "27/11/2026 09:30", "", List.of()), "fechaHora", "posteriores");
        assertError(gestor.registrarRealizacion(1, "26/11/2026 09:30", "x".repeat(1001), List.of()), "observaciones",
                "1000");
        assertError(gestor.registrarRealizacion(1, "26/11/2026 09:30", "",
                List.of(new NuevaIncidencia(TipoIncidencia.OTRA, " "))), "descripcion", "Descripción");
        assertEquals(2, servicio.listarPendientes(MEDICO, null).size());
    }

    @Test
    void erroresDelServicio() {
        assertError(gestor.registrarRealizacion(99, "26/11/2026 09:30", "", List.of()), null, "No existe esa solicitud");
        gestor.registrarCancelacion(1, "Ausente");
        ResultadoPruebas<Prueba> r = gestor.registrarRealizacion(1, "26/11/2026 09:30", "", List.of());
        assertError(r, null, "Es la prueba número 1");
    }

    @Test
    void registrarCancelacion() {
        ResultadoPruebas<Prueba> r = gestor.registrarCancelacion(1, " Ausente ");
        assertTrue(r.exito());
        assertEquals("Ausente", r.valor().motivoCancelacion());
        assertError(gestor.registrarCancelacion(2, " "), "motivo", "Motivo");
        assertError(gestor.registrarCancelacion(2, "x".repeat(256)), "motivo", "255");
    }

    @Test
    void anadirIncidencia() {
        Prueba p = gestor.registrarRealizacion(1, "26/11/2026 09:30", "", List.of()).valor();
        ResultadoPruebas<Incidencia> r = gestor.anadirIncidencia(p.id(), TipoIncidencia.MATERIAL, " Falta placa ");
        assertTrue(r.exito());
        assertEquals("Falta placa", r.valor().descripcion());
        assertError(gestor.anadirIncidencia(p.id(), null, "x"), "tipo", "Tipo de incidencia");
        assertError(gestor.anadirIncidencia(p.id(), TipoIncidencia.OTRA, ""), "descripcion", "Descripción");
        assertError(gestor.anadirIncidencia(99, TipoIncidencia.OTRA, "x"), null, "No existe esa prueba");
    }

    @Test
    void buscar() {
        gestor.registrarRealizacion(1, "26/11/2026 09:30", "", List.of());
        gestor.registrarCancelacion(2, "Ausente");
        ResultadoPruebas<List<ResumenPrueba>> todas = gestor.buscar("", "", "", "", null);
        assertEquals(2, todas.valor().size());
        assertEquals("", todas.mensaje());
        assertEquals(1, gestor.buscar("12345678Z", "1", "26/11/2026", "26/11/2026", EstadoPrueba.REALIZADA)
                .valor().size());
        assertEquals("No hay pruebas que cumplan los filtros.",
                gestor.buscar("", "", "27/11/2026", "", null).mensaje());
    }

    @Test
    void buscarConFiltrosNoValidos() {
        assertError(gestor.buscar("123", "", "", "", null), "dni", "DNI");
        assertError(gestor.buscar("", "abc", "", "", null), "idSolicitud", "Solicitud");
        assertError(gestor.buscar("", "", "ayer", "", null), "desde", "Desde");
        assertError(gestor.buscar("", "", "", "mañana", null), "hasta", "Hasta");
        assertError(gestor.buscar("", "", "26/11/2026", "25/11/2026", null), "hasta", "Hasta");
    }

    @Test
    void obtener() {
        Prueba p = gestor.registrarCancelacion(1, "Ausente").valor();
        assertEquals(p, gestor.obtener(p.id()).valor());
        assertError(gestor.obtener(99), null, "No existe esa prueba");
    }

    @Test
    void sinConexionSeIndica() {
        ServicioPruebas caido = new ServicioPruebasEnMemoria() {
            @Override
            public Prueba obtener(String idMedico, long idPrueba) {
                throw new ExcepcionServicioPruebas(CodigoError.SIN_CONEXION);
            }
        };
        ResultadoPruebas<Prueba> r = new GestorPruebasCliente(caido, MEDICO).obtener(1);
        assertTrue(r.sinConexion());
        assertFalse(r.exito());
        assertTrue(r.mensaje().contains("No hay conexión"));
    }

    @Test
    void formatos() {
        assertEquals("—", FormatosPruebas.aPantalla((LocalDateTime) null));
        assertEquals("26/11/2026 09:30", FormatosPruebas.aPantalla(LocalDateTime.of(2026, 11, 26, 9, 30)));
        assertEquals("Realizada", FormatosPruebas.aPantalla(EstadoPrueba.REALIZADA));
        assertEquals("Cancelada", FormatosPruebas.aPantalla(EstadoPrueba.CANCELADA));
        assertEquals("Técnica", FormatosPruebas.aPantalla(TipoIncidencia.TECNICA));
        assertEquals("Del paciente", FormatosPruebas.aPantalla(TipoIncidencia.PACIENTE));
        assertEquals("De material", FormatosPruebas.aPantalla(TipoIncidencia.MATERIAL));
        assertEquals("Otra", FormatosPruebas.aPantalla(TipoIncidencia.OTRA));
        assertTrue(FormatosPruebas.fechaHora("31/02/2026 10:00").isEmpty());
        assertTrue(FormatosPruebas.fecha(null).isEmpty());
        assertEquals("", MensajesPruebas.etiqueta(null));
        assertEquals("Médico", MensajesPruebas.etiqueta("idMedico"));
        assertEquals("Prueba", MensajesPruebas.etiqueta("idPrueba"));
        assertEquals("Estado", MensajesPruebas.etiqueta("estado"));
        assertEquals("un valor válido", MensajesPruebas.formatoEsperado("otro"));
        assertEquals("de 1 a 20 caracteres", MensajesPruebas.formatoEsperado("idMedico"));
        assertEquals("Hay un campo con un formato no válido.",
                MensajesPruebas.mensajeDeCampo(CodigoError.CAMPO_NO_VALIDO, null));
        assertEquals("Falta un campo obligatorio.", MensajesPruebas.mensajeDeCampo(CodigoError.CAMPO_OBLIGATORIO, null));
    }
}
