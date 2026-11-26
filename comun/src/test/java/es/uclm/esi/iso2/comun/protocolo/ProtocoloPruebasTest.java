package es.uclm.esi.iso2.comun.protocolo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import es.uclm.esi.iso2.comun.dominio.EstadoPrueba;
import es.uclm.esi.iso2.comun.dominio.FiltroPruebas;
import es.uclm.esi.iso2.comun.dominio.Incidencia;
import es.uclm.esi.iso2.comun.dominio.NuevaIncidencia;
import es.uclm.esi.iso2.comun.dominio.Prueba;
import es.uclm.esi.iso2.comun.dominio.ResumenPrueba;
import es.uclm.esi.iso2.comun.dominio.SolicitudPendiente;
import es.uclm.esi.iso2.comun.dominio.TipoIncidencia;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

class ProtocoloPruebasTest {

    private static final LocalDateTime REGISTRO = LocalDateTime.of(2026, 11, 26, 10, 5, 7);
    private static final Incidencia INCIDENCIA =
            new Incidencia(3, 7, TipoIncidencia.TECNICA, "Fallo; del equipo\ny reinicio", REGISTRO);
    private static final Prueba REALIZADA = new Prueba(7, 4, "12345678Z", "medico1", EstadoPrueba.REALIZADA,
            LocalDateTime.of(2026, 11, 26, 9, 30), "Sin; novedad", "", REGISTRO, List.of(INCIDENCIA));
    private static final Prueba CANCELADA = new Prueba(8, 5, "12345678Z", "medico1", EstadoPrueba.CANCELADA, null,
            "", "Paciente ausente", REGISTRO, List.of());

    private static List<String> campos(String respuesta) {
        List<String> campos = Protocolo.descomponer(respuesta);
        assertEquals("OK", campos.get(0));
        return campos.subList(1, campos.size());
    }

    @Test
    void pruebaRealizadaVaYVuelve() {
        String linea = ProtocoloPruebas.respuestaOk(REALIZADA);
        assertEquals(REALIZADA, ProtocoloPruebas.pruebaDesdeCampos(campos(linea)));
    }

    @Test
    void pruebaCanceladaVaYVuelve() {
        Prueba vuelta = ProtocoloPruebas.pruebaDesdeCampos(campos(ProtocoloPruebas.respuestaOk(CANCELADA)));
        assertEquals(CANCELADA, vuelta);
        assertNull(vuelta.fechaHoraRealizacion());
    }

    @Test
    void pruebaNoValida() {
        assertThrows(IllegalArgumentException.class, () -> ProtocoloPruebas.pruebaDesdeCampos(List.of("1")));
        List<String> mal = new java.util.ArrayList<>(campos(ProtocoloPruebas.respuestaOk(REALIZADA)));
        mal.add("sobra");
        assertThrows(IllegalArgumentException.class, () -> ProtocoloPruebas.pruebaDesdeCampos(mal));
        List<String> estado = new java.util.ArrayList<>(campos(ProtocoloPruebas.respuestaOk(CANCELADA)));
        estado.set(4, "OTRO");
        assertThrows(IllegalArgumentException.class, () -> ProtocoloPruebas.pruebaDesdeCampos(estado));
    }

    @Test
    void incidenciaVaYVuelve() {
        List<String> c = campos(ProtocoloPruebas.respuestaOk(INCIDENCIA));
        assertEquals(INCIDENCIA, ProtocoloPruebas.incidenciaDesdeCampos(c, 0, 7));
        assertThrows(IllegalArgumentException.class,
                () -> ProtocoloPruebas.incidenciaDesdeCampos(List.of("1", "NO", "x", "y"), 0, 7));
    }

    @Test
    void listaDePendientes() {
        SolicitudPendiente p = new SolicitudPendiente(4, "12345678Z", "Lucía García", "Mamografía", "URGENTE", 9,
                LocalDateTime.of(2026, 12, 1, 10, 0));
        List<SolicitudPendiente> vuelta = ProtocoloPruebas.pendientesDesdeCampos(
                campos(ProtocoloPruebas.respuestaPendientes(List.of(p, p))));
        assertEquals(List.of(p, p), vuelta);
        assertEquals(List.of(), ProtocoloPruebas.pendientesDesdeCampos(
                campos(ProtocoloPruebas.respuestaPendientes(List.of()))));
        assertThrows(IllegalArgumentException.class, () -> ProtocoloPruebas.pendientesDesdeCampos(List.of("1", "x")));
        assertThrows(IllegalArgumentException.class, () -> ProtocoloPruebas.pendientesDesdeCampos(List.of()));
        assertThrows(IllegalArgumentException.class, () -> ProtocoloPruebas.pendientesDesdeCampos(List.of("a")));
        assertThrows(IllegalArgumentException.class,
                () -> ProtocoloPruebas.pendientesDesdeCampos(List.of("1", "x", "y", "z", "t", "u", "v", "w")));
    }

    @Test
    void listaDeResumenes() {
        ResumenPrueba r = REALIZADA.resumen();
        ResumenPrueba c = CANCELADA.resumen();
        assertEquals(1, r.numIncidencias());
        assertEquals(List.of(r, c),
                ProtocoloPruebas.resumenesDesdeCampos(campos(ProtocoloPruebas.respuestaResumenes(List.of(r, c)))));
        assertThrows(IllegalArgumentException.class, () -> ProtocoloPruebas.resumenesDesdeCampos(List.of("1")));
    }

    @Test
    void peticiones() {
        assertEquals("LISTAR_SOLICITUDES_PENDIENTES;medico1;", ProtocoloPruebas.peticionListarPendientes("medico1", null));
        assertEquals("REGISTRAR_PRUEBA;medico1;4;2026-11-26T09:30;obs\\;1;1;TECNICA;fallo",
                ProtocoloPruebas.peticionRegistrar("medico1", 4, LocalDateTime.of(2026, 11, 26, 9, 30), "obs;1",
                        List.of(new NuevaIncidencia(TipoIncidencia.TECNICA, "fallo"))));
        assertEquals("REGISTRAR_PRUEBA;medico1;4;;;1;;x", ProtocoloPruebas.peticionRegistrar("medico1", 4, null, null,
                List.of(new NuevaIncidencia(null, "x"))));
        assertEquals("CANCELAR_PRUEBA;medico1;4;ausente", ProtocoloPruebas.peticionCancelar("medico1", 4, "ausente"));
        assertEquals("ANADIR_INCIDENCIA;medico1;7;OTRA;algo",
                ProtocoloPruebas.peticionAnadirIncidencia("medico1", 7, new NuevaIncidencia(TipoIncidencia.OTRA, "algo")));
        assertEquals("ANADIR_INCIDENCIA;medico1;7;;",
                ProtocoloPruebas.peticionAnadirIncidencia("medico1", 7, new NuevaIncidencia(null, null)));
        assertEquals("BUSCAR_PRUEBAS;medico1;;;;;", ProtocoloPruebas.peticionBuscar("medico1", FiltroPruebas.SIN_FILTROS));
        assertEquals("BUSCAR_PRUEBAS;medico1;12345678Z;4;2026-11-01;2026-11-30;CANCELADA",
                ProtocoloPruebas.peticionBuscar("medico1", new FiltroPruebas("12345678Z", 4L,
                        LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 30), EstadoPrueba.CANCELADA)));
        assertEquals("OBTENER_PRUEBA;medico1;7", ProtocoloPruebas.peticionObtener("medico1", 7));
    }
}
