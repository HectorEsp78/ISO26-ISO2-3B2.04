package es.uclm.esi.iso2.servidor.pruebas;

import static es.uclm.esi.iso2.servidor.pruebas.EntornoPruebas.AHORA;
import static es.uclm.esi.iso2.servidor.pruebas.EntornoPruebas.DNI;
import static es.uclm.esi.iso2.servidor.pruebas.EntornoPruebas.MEDICO;
import static es.uclm.esi.iso2.servidor.pruebas.EntornoPruebas.OTRO_MEDICO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import es.uclm.esi.iso2.comun.dominio.EstadoCita;
import es.uclm.esi.iso2.comun.dominio.EstadoPrueba;
import es.uclm.esi.iso2.comun.dominio.FiltroPruebas;
import es.uclm.esi.iso2.comun.dominio.Incidencia;
import es.uclm.esi.iso2.comun.dominio.NuevaIncidencia;
import es.uclm.esi.iso2.comun.dominio.Prueba;
import es.uclm.esi.iso2.comun.dominio.ResumenPrueba;
import es.uclm.esi.iso2.comun.dominio.SolicitudPendiente;
import es.uclm.esi.iso2.comun.dominio.TipoIncidencia;
import es.uclm.esi.iso2.comun.protocolo.CodigoError;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Pruebas de {@link PruebaService} sobre H2, con CU13 real y CU06 y CU12 simulados.
 * «Ahora» es el 26/11/2026 a las 10:00.
 */
class PruebaServiceTest {

    private static final LocalDateTime REALIZADA_EN = LocalDateTime.of(2026, 11, 26, 9, 30);
    private static final NuevaIncidencia INCIDENCIA = new NuevaIncidencia(TipoIncidencia.TECNICA, "Fallo del equipo");

    private EntornoPruebas entorno;
    private PruebaService servicio;

    @BeforeEach
    void preparar() {
        entorno = new EntornoPruebas();
        servicio = entorno.servicio;
    }

    private static void assertCodigo(CodigoError codigo, String detalle, Runnable accion) {
        PruebaException e = assertThrows(PruebaException.class, accion::run);
        assertEquals(codigo, e.codigo());
        assertEquals(detalle, e.detalle());
    }

    private EstadoCita estadoCita(SolicitudDePrueba s) {
        return entorno.citas.obtenerCita(s.idCita()).estado();
    }

    // ---- Flujo 6.1: solicitudes pendientes ----

    @Test
    void pendientesDelMedicoOrdenadasPorCita() {
        SolicitudDePrueba tarde = entorno.solicitudConCita(MEDICO, 12);
        SolicitudDePrueba pronto = entorno.solicitudConCita(MEDICO, 9);
        entorno.solicitudConCita(OTRO_MEDICO, 11);
        entorno.solicitudes.anadir(MEDICO, DNI, null);

        List<SolicitudPendiente> pendientes = servicio.buscarSolicitudesPendientes(MEDICO, "");

        assertEquals(List.of(pronto.idSolicitud(), tarde.idSolicitud()),
                pendientes.stream().map(SolicitudPendiente::idSolicitud).toList());
        assertEquals("Lucía García Martín", pendientes.get(0).nombrePaciente());
        assertEquals(LocalDateTime.of(2026, 12, 1, 9, 0), pendientes.get(0).inicioCita());
    }

    @Test
    void pendientesNoIncluyeLasQueYaTienenPruebaNiCitaNoProgramada() {
        SolicitudDePrueba hecha = entorno.solicitudConCita(MEDICO, 9);
        SolicitudDePrueba cancelada = entorno.solicitudConCita(MEDICO, 10);
        SolicitudDePrueba abierta = entorno.solicitudConCita(MEDICO, 11);
        servicio.registrarRealizacion(MEDICO, hecha.idSolicitud(), REALIZADA_EN, "", List.of());
        entorno.citas.cancelarCita(cancelada.idCita(), "Cambio de planes");
        entorno.solicitudes.anadir(MEDICO, DNI, 9999L);

        List<SolicitudPendiente> pendientes = servicio.buscarSolicitudesPendientes(MEDICO, " " + DNI.toLowerCase());

        assertEquals(List.of(abierta.idSolicitud()),
                pendientes.stream().map(SolicitudPendiente::idSolicitud).toList());
        assertTrue(servicio.buscarSolicitudesPendientes(MEDICO, "99999999R").isEmpty());
    }

    @Test
    void pendientesSinNombreUsaElDni() {
        entorno.solicitudes.anadir(MEDICO, "X1234567L", entorno.solicitudConCita(MEDICO, 9).idCita());
        List<SolicitudPendiente> pendientes = servicio.buscarSolicitudesPendientes(MEDICO, "X1234567L");
        assertEquals("X1234567L", pendientes.get(0).nombrePaciente());
    }

    @Test
    void pendientesExigeMedico() {
        assertCodigo(CodigoError.CAMPO_OBLIGATORIO, "idMedico", () -> servicio.buscarSolicitudesPendientes(" ", null));
    }

    // ---- Flujo 6.2: realización ----

    @Test
    void registrarRealizacionGuardaLaPruebaYCambiaCitaYSolicitud() {
        SolicitudDePrueba s = entorno.solicitudConCita(MEDICO, 9);

        Prueba prueba = servicio.registrarRealizacion(MEDICO, s.idSolicitud(), REALIZADA_EN, "  Sin novedad ",
                List.of(INCIDENCIA));

        assertTrue(prueba.id() > 0);
        assertEquals(EstadoPrueba.REALIZADA, prueba.estado());
        assertEquals(DNI, prueba.dniPaciente());
        assertEquals("Sin novedad", prueba.observaciones());
        assertEquals(REALIZADA_EN, prueba.fechaHoraRealizacion());
        assertEquals(AHORA, prueba.fechaRegistro());
        assertEquals(1, prueba.incidencias().size());
        assertEquals(prueba.id(), prueba.incidencias().get(0).idPrueba());
        assertEquals(EstadoCita.REALIZADA, estadoCita(s));
        assertEquals("REALIZADA", entorno.solicitudes.estados.get(s.idSolicitud()));
        assertEquals(prueba, servicio.consultarPrueba(MEDICO, prueba.id()));
    }

    @Test
    void registrarRealizacionConErroresDeValidacion() {
        SolicitudDePrueba s = entorno.solicitudConCita(MEDICO, 9);
        long id = s.idSolicitud();
        assertCodigo(CodigoError.CAMPO_OBLIGATORIO, "fechaHora",
                () -> servicio.registrarRealizacion(MEDICO, id, null, "", List.of()));
        assertCodigo(CodigoError.FECHA_FUTURA, "fechaHora",
                () -> servicio.registrarRealizacion(MEDICO, id, AHORA.plusMinutes(1), "", List.of()));
        assertCodigo(CodigoError.CAMPO_NO_VALIDO, "observaciones",
                () -> servicio.registrarRealizacion(MEDICO, id, REALIZADA_EN, "x".repeat(1001), List.of()));
        assertCodigo(CodigoError.CAMPO_OBLIGATORIO, "descripcion", () -> servicio.registrarRealizacion(MEDICO, id,
                REALIZADA_EN, "", List.of(new NuevaIncidencia(TipoIncidencia.OTRA, ""))));
        assertCodigo(CodigoError.CAMPO_OBLIGATORIO, "idMedico",
                () -> servicio.registrarRealizacion("", id, REALIZADA_EN, "", List.of()));
        assertEquals(EstadoCita.PROGRAMADA, estadoCita(s));
        assertTrue(entorno.dao.buscarPorSolicitud(id).isEmpty());
    }

    @Test
    void registrarRealizacionSinIncidenciasAceptaNulo() {
        SolicitudDePrueba s = entorno.solicitudConCita(MEDICO, 9);
        Prueba prueba = servicio.registrarRealizacion(MEDICO, s.idSolicitud(), REALIZADA_EN, null, null);
        assertEquals("", prueba.observaciones());
        assertTrue(prueba.incidencias().isEmpty());
    }

    @Test
    void registrarRechazaSolicitudesQueNoSePuedenUsar() {
        SolicitudDePrueba ajena = entorno.solicitudConCita(OTRO_MEDICO, 9);
        SolicitudDePrueba sinCita = entorno.solicitudes.anadir(MEDICO, DNI, null);
        SolicitudDePrueba citaInexistente = entorno.solicitudes.anadir(MEDICO, DNI, 9999L);
        SolicitudDePrueba citaCancelada = entorno.solicitudConCita(MEDICO, 10);
        entorno.citas.cancelarCita(citaCancelada.idCita(), "Cambio de planes");

        assertCodigo(CodigoError.SOLICITUD_NO_ENCONTRADA, null,
                () -> servicio.registrarRealizacion(MEDICO, 777, REALIZADA_EN, "", List.of()));
        assertCodigo(CodigoError.SOLICITUD_NO_PERMITIDA, null,
                () -> servicio.registrarRealizacion(MEDICO, ajena.idSolicitud(), REALIZADA_EN, "", List.of()));
        for (SolicitudDePrueba s : List.of(sinCita, citaInexistente, citaCancelada)) {
            assertCodigo(CodigoError.SOLICITUD_SIN_CITA, null,
                    () -> servicio.registrarRealizacion(MEDICO, s.idSolicitud(), REALIZADA_EN, "", List.of()));
        }
    }

    @Test
    void unaSolicitudSoloTieneUnaPrueba() {
        SolicitudDePrueba s = entorno.solicitudConCita(MEDICO, 9);
        Prueba primera = servicio.registrarRealizacion(MEDICO, s.idSolicitud(), REALIZADA_EN, "", List.of());
        assertCodigo(CodigoError.PRUEBA_YA_EXISTE, String.valueOf(primera.id()),
                () -> servicio.registrarRealizacion(MEDICO, s.idSolicitud(), REALIZADA_EN, "", List.of()));
        assertCodigo(CodigoError.PRUEBA_YA_EXISTE, String.valueOf(primera.id()),
                () -> servicio.registrarCancelacion(MEDICO, s.idSolicitud(), "Motivo"));
    }

    @Test
    void elIndiceUniqueEvitaElDuplicadoAunqueSeSalteLaComprobacion() {
        SolicitudDePrueba s = entorno.solicitudConCita(MEDICO, 9);
        Prueba primera = servicio.registrarCancelacion(MEDICO, s.idSolicitud(), "Motivo");
        Prueba otra = new Prueba(0, s.idSolicitud(), DNI, MEDICO, EstadoPrueba.CANCELADA, null, "", "Otro", AHORA,
                List.of());
        assertCodigo(CodigoError.PRUEBA_YA_EXISTE, String.valueOf(primera.id()), () -> entorno.dao.guardar(otra));
    }

    // ---- Flujo 6.3: cancelación ----

    @Test
    void registrarCancelacionCancelaCitaYSolicitud() {
        SolicitudDePrueba s = entorno.solicitudConCita(MEDICO, 9);

        Prueba prueba = servicio.registrarCancelacion(MEDICO, s.idSolicitud(), " Paciente ausente ");

        assertEquals(EstadoPrueba.CANCELADA, prueba.estado());
        assertEquals("Paciente ausente", prueba.motivoCancelacion());
        assertEquals(null, prueba.fechaHoraRealizacion());
        assertEquals(EstadoCita.CANCELADA, estadoCita(s));
        assertEquals("Paciente ausente", entorno.citas.obtenerCita(s.idCita()).motivoCancelacion());
        assertEquals("CANCELADA", entorno.solicitudes.estados.get(s.idSolicitud()));
    }

    @Test
    void registrarCancelacionConErrores() {
        SolicitudDePrueba s = entorno.solicitudConCita(MEDICO, 9);
        assertCodigo(CodigoError.CAMPO_OBLIGATORIO, "motivo",
                () -> servicio.registrarCancelacion(MEDICO, s.idSolicitud(), "  "));
        assertCodigo(CodigoError.CAMPO_NO_VALIDO, "motivo",
                () -> servicio.registrarCancelacion(MEDICO, s.idSolicitud(), "x".repeat(256)));
        assertCodigo(CodigoError.CAMPO_OBLIGATORIO, "idMedico",
                () -> servicio.registrarCancelacion(null, s.idSolicitud(), "Motivo"));
        assertCodigo(CodigoError.SOLICITUD_NO_PERMITIDA, null,
                () -> servicio.registrarCancelacion(OTRO_MEDICO, s.idSolicitud(), "Motivo"));
    }

    // ---- DEC-076: compensación ----

    @Test
    void siFallaLaSolicitudSeBorraLaPrueba() {
        SolicitudDePrueba s = entorno.solicitudConCita(MEDICO, 9);
        entorno.solicitudes.fallaAlMarcar = true;

        assertCodigo(CodigoError.ERROR_INTERNO, null,
                () -> servicio.registrarRealizacion(MEDICO, s.idSolicitud(), REALIZADA_EN, "", List.of(INCIDENCIA)));

        assertTrue(entorno.dao.buscarPorSolicitud(s.idSolicitud()).isEmpty());
        assertTrue(servicio.buscarPruebas(MEDICO, FiltroPruebas.SIN_FILTROS).isEmpty());
        // Riesgo conocido del DEC-076: la cita ya cambió y CU13 no permite deshacerlo.
        assertEquals(EstadoCita.REALIZADA, estadoCita(s));
        entorno.solicitudes.fallaAlMarcar = false;
        assertCodigo(CodigoError.SOLICITUD_SIN_CITA, null,
                () -> servicio.registrarCancelacion(MEDICO, s.idSolicitud(), "Motivo"));
    }

    @Test
    void siFallaLaCitaSeBorraLaPrueba() {
        SolicitudDePrueba s = entorno.solicitudConCita(MEDICO, 9);
        // La cita pasa a otro estado entre la comprobación y el cambio: se simula con otro servicio de citas.
        PruebaService conCitaRota = new PruebaService(entorno.dao, new CitasQueFallan(entorno.citas),
                entorno.solicitudes, new EntornoPruebas.PacientesSimulados(), EntornoPruebas.RELOJ);

        assertCodigo(CodigoError.ERROR_INTERNO, null,
                () -> conCitaRota.registrarCancelacion(MEDICO, s.idSolicitud(), "Motivo"));

        assertTrue(entorno.dao.buscarPorSolicitud(s.idSolicitud()).isEmpty());
        assertEquals("PENDIENTE", entorno.solicitudes.estados.get(s.idSolicitud()));
    }

    // ---- Flujo 6.5: incidencias ----

    @Test
    void anadirIncidenciaAUnaPruebaDelMedico() {
        SolicitudDePrueba s = entorno.solicitudConCita(MEDICO, 9);
        Prueba prueba = servicio.registrarRealizacion(MEDICO, s.idSolicitud(), REALIZADA_EN, "", List.of());

        Incidencia incidencia = servicio.anadirIncidencia(MEDICO, prueba.id(),
                new NuevaIncidencia(TipoIncidencia.PACIENTE, " Se movió "));

        assertTrue(incidencia.id() > 0);
        assertEquals("Se movió", incidencia.descripcion());
        assertEquals(AHORA, incidencia.fechaHora());
        assertEquals(List.of(incidencia), servicio.consultarPrueba(MEDICO, prueba.id()).incidencias());
    }

    @Test
    void anadirIncidenciaConErrores() {
        SolicitudDePrueba s = entorno.solicitudConCita(MEDICO, 9);
        Prueba prueba = servicio.registrarRealizacion(MEDICO, s.idSolicitud(), REALIZADA_EN, "", List.of());
        assertCodigo(CodigoError.CAMPO_OBLIGATORIO, "tipo",
                () -> servicio.anadirIncidencia(MEDICO, prueba.id(), new NuevaIncidencia(null, "x")));
        assertCodigo(CodigoError.CAMPO_OBLIGATORIO, "idMedico",
                () -> servicio.anadirIncidencia("", prueba.id(), INCIDENCIA));
        assertCodigo(CodigoError.PRUEBA_NO_ENCONTRADA, null, () -> servicio.anadirIncidencia(MEDICO, 999, INCIDENCIA));
        assertCodigo(CodigoError.PRUEBA_NO_PERMITIDA, null,
                () -> servicio.anadirIncidencia(OTRO_MEDICO, prueba.id(), INCIDENCIA));
    }

    // ---- Flujos 6.4 y 6.6: consultas ----

    @Test
    void buscarPruebasConFiltros() {
        SolicitudDePrueba a = entorno.solicitudConCita(MEDICO, 9);
        SolicitudDePrueba b = entorno.solicitudConCita(MEDICO, 10);
        SolicitudDePrueba ajena = entorno.solicitudConCita(OTRO_MEDICO, 11);
        Prueba pa = servicio.registrarRealizacion(MEDICO, a.idSolicitud(), REALIZADA_EN, "", List.of(INCIDENCIA));
        Prueba pb = servicio.registrarCancelacion(MEDICO, b.idSolicitud(), "Motivo");
        servicio.registrarCancelacion(OTRO_MEDICO, ajena.idSolicitud(), "Motivo");
        LocalDate hoy = AHORA.toLocalDate();

        List<ResumenPrueba> todas = servicio.buscarPruebas(MEDICO, null);
        assertEquals(List.of(pb.id(), pa.id()), todas.stream().map(ResumenPrueba::id).toList());
        assertEquals(1, todas.get(1).numIncidencias());
        assertEquals(List.of(pb.id()), ids(servicio.buscarPruebas(MEDICO,
                new FiltroPruebas(null, null, null, null, EstadoPrueba.CANCELADA))));
        assertEquals(List.of(pa.id()), ids(servicio.buscarPruebas(MEDICO,
                new FiltroPruebas(null, a.idSolicitud(), null, null, null))));
        assertEquals(2, servicio.buscarPruebas(MEDICO, new FiltroPruebas(DNI.toLowerCase(), null, hoy, hoy, null))
                .size());
        assertTrue(servicio.buscarPruebas(MEDICO, new FiltroPruebas("X1234567L", null, null, null, null)).isEmpty());
        assertTrue(servicio.buscarPruebas(MEDICO, new FiltroPruebas(null, null, hoy.plusDays(1), null, null))
                .isEmpty());
        assertTrue(servicio.buscarPruebas(MEDICO, new FiltroPruebas(null, null, null, hoy.minusDays(1), null))
                .isEmpty());
    }

    @Test
    void buscarPruebasConFiltroNoValido() {
        LocalDate hoy = AHORA.toLocalDate();
        assertCodigo(CodigoError.CAMPO_NO_VALIDO, "hasta", () -> servicio.buscarPruebas(MEDICO,
                new FiltroPruebas(null, null, hoy, hoy.minusDays(1), null)));
        assertCodigo(CodigoError.CAMPO_OBLIGATORIO, "idMedico",
                () -> servicio.buscarPruebas(null, FiltroPruebas.SIN_FILTROS));
    }

    @Test
    void consultarPrueba() {
        SolicitudDePrueba s = entorno.solicitudConCita(MEDICO, 9);
        Prueba prueba = servicio.registrarCancelacion(MEDICO, s.idSolicitud(), "Motivo");
        assertEquals(prueba, servicio.consultarPrueba(MEDICO, prueba.id()));
        assertCodigo(CodigoError.PRUEBA_NO_ENCONTRADA, null, () -> servicio.consultarPrueba(MEDICO, 999));
        assertCodigo(CodigoError.PRUEBA_NO_PERMITIDA, null, () -> servicio.consultarPrueba(OTRO_MEDICO, prueba.id()));
        assertCodigo(CodigoError.CAMPO_OBLIGATORIO, "idMedico", () -> servicio.consultarPrueba(" ", prueba.id()));
    }

    @Test
    void obtenerPruebaParaCu09() {
        SolicitudDePrueba s = entorno.solicitudConCita(MEDICO, 9);
        Prueba prueba = servicio.registrarRealizacion(MEDICO, s.idSolicitud(), REALIZADA_EN, "", List.of());
        assertEquals(new DatosPrueba(prueba.id(), DNI, MEDICO, EstadoPrueba.REALIZADA),
                servicio.obtenerPrueba(prueba.id()));
        assertCodigo(CodigoError.PRUEBA_NO_ENCONTRADA, null, () -> servicio.obtenerPrueba(999));
    }

    @Test
    void borrarSinPruebaNoHaceNada() {
        entorno.dao.borrar(12345);
        assertTrue(entorno.dao.solicitudesConPrueba(List.of()).isEmpty());
    }

    @Test
    void sinCu06NoHaySolicitudes() {
        assertTrue(SolicitudesDePrueba.SIN_CU06.buscar(1).isEmpty());
        assertTrue(SolicitudesDePrueba.SIN_CU06.conCitaDelMedico(MEDICO, null).isEmpty());
        assertThrows(IllegalStateException.class, () -> SolicitudesDePrueba.SIN_CU06.marcarRealizada(1));
        assertThrows(IllegalStateException.class, () -> SolicitudesDePrueba.SIN_CU06.marcarCancelada(1));
    }

    private static List<Long> ids(List<ResumenPrueba> resumenes) {
        return resumenes.stream().map(ResumenPrueba::id).toList();
    }
}
