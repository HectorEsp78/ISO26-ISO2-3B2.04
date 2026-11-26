package es.uclm.esi.iso2.comun.protocolo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import es.uclm.esi.iso2.comun.dominio.Cita;
import es.uclm.esi.iso2.comun.dominio.DatosCita;
import es.uclm.esi.iso2.comun.dominio.EstadoCita;
import es.uclm.esi.iso2.comun.dominio.Recurso;
import es.uclm.esi.iso2.comun.dominio.TipoPrueba;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

class ProtocoloCitasTest {

    private static final LocalDateTime INICIO = LocalDateTime.of(2026, 12, 1, 10, 30);
    private static final Cita CITA = new Cita(7, "12345678Z", 2, 3, INICIO, 30, EstadoCita.CANCELADA,
            "Avisa; no puede\nvenir");

    @Test
    void unaCitaSonNueveCamposConElFinCalculado() {
        assertEquals(List.of("7", "12345678Z", "2", "3", "2026-12-01T10:30", "30", "2026-12-01T11:00",
                "CANCELADA", "Avisa; no puede\nvenir"), ProtocoloCitas.camposCita(CITA));
    }

    @Test
    void respuestaOkConCitaSeLeeIgual() {
        Respuesta r = Respuesta.leer(ProtocoloCitas.respuestaOk(CITA));
        assertTrue(r.correcta());
        assertEquals(CITA, ProtocoloCitas.citaDesdeCampos(r.datos(), 0));
    }

    @Test
    void listaDeCitasIdaYVuelta() {
        Cita otra = new Cita(8, "X1234567L", 1, 1, INICIO.plusDays(1), 15, EstadoCita.PROGRAMADA, "");
        List<String> datos = Respuesta.leer(ProtocoloCitas.respuestaCitas(List.of(CITA, otra))).datos();
        assertEquals("2", datos.get(0));
        assertEquals(List.of(CITA, otra), ProtocoloCitas.citasDesdeCampos(datos));
        assertEquals(List.of(), ProtocoloCitas.citasDesdeCampos(List.of("0")));
    }

    @Test
    void tiposYRecursosIdaYVuelta() {
        List<TipoPrueba> tipos = List.of(new TipoPrueba(1, "Radiografía; tórax", 15), new TipoPrueba(2, "TAC", 30));
        List<Recurso> recursos = List.of(new Recurso(4, "Sala 1"));
        assertEquals(tipos, ProtocoloCitas.tiposDesdeCampos(
                Respuesta.leer(ProtocoloCitas.respuestaTipos(tipos)).datos()));
        assertEquals(recursos, ProtocoloCitas.recursosDesdeCampos(
                Respuesta.leer(ProtocoloCitas.respuestaRecursos(recursos)).datos()));
    }

    @Test
    void listasMalFormadas() {
        assertThrows(IllegalArgumentException.class, () -> ProtocoloCitas.citasDesdeCampos(List.of()));
        assertThrows(IllegalArgumentException.class, () -> ProtocoloCitas.citasDesdeCampos(List.of("1", "x")));
        assertThrows(IllegalArgumentException.class, () -> ProtocoloCitas.tiposDesdeCampos(List.of("-1")));
        assertThrows(IllegalArgumentException.class, () -> ProtocoloCitas.recursosDesdeCampos(List.of("1", "1")));
        assertThrows(IllegalArgumentException.class, () -> ProtocoloCitas.citasDesdeCampos(List.of("uno")));
    }

    @Test
    void citaMalFormada() {
        List<String> campos = new java.util.ArrayList<>(ProtocoloCitas.camposCita(CITA));
        assertThrows(IllegalArgumentException.class, () -> ProtocoloCitas.citaDesdeCampos(campos, 1));
        campos.set(7, "INVENTADO");
        assertThrows(IllegalArgumentException.class, () -> ProtocoloCitas.citaDesdeCampos(campos, 0));
    }

    @Test
    void peticiones() {
        assertEquals("ASIGNAR_CITA;12345678Z;1;2;2026-12-01T10:30;",
                ProtocoloCitas.peticionAsignar(new DatosCita("12345678Z", 1, 2, INICIO, null)));
        assertEquals("ASIGNAR_CITA;;;;;45",
                ProtocoloCitas.peticionAsignar(new DatosCita(null, null, null, null, 45)));
        assertEquals("BUSCAR_CITAS;12345678Z;2026-12-01;2026-12-31;PROGRAMADA",
                ProtocoloCitas.peticionBuscar("12345678Z", LocalDate.of(2026, 12, 1), LocalDate.of(2026, 12, 31),
                        EstadoCita.PROGRAMADA));
        assertEquals("BUSCAR_CITAS;;;;", ProtocoloCitas.peticionBuscar(null, null, null, null));
        assertEquals("REPROGRAMAR_CITA;7;3;2026-12-01T10:30;20",
                ProtocoloCitas.peticionReprogramar(7, 3, INICIO, 20));
        assertEquals("REPROGRAMAR_CITA;7;;;", ProtocoloCitas.peticionReprogramar(7, null, null, null));
        assertEquals("CANCELAR_CITA;7;No puede\\; avisa", ProtocoloCitas.peticionCancelar(7, "No puede; avisa"));
    }

    @Test
    void errorConYSinDetalle() {
        assertEquals("ERROR;SOLAPE_RECURSO;12", ProtocoloCitas.respuestaError(CodigoError.SOLAPE_RECURSO, "12"));
        assertEquals("ERROR;CITA_NO_ENCONTRADA", ProtocoloCitas.respuestaError(CodigoError.CITA_NO_ENCONTRADA, null));
        assertEquals("ERROR;FECHA_PASADA", ProtocoloCitas.respuestaError(CodigoError.FECHA_PASADA, ""));
    }
}
