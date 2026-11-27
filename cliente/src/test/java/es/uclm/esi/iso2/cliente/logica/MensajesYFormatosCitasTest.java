package es.uclm.esi.iso2.cliente.logica;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import es.uclm.esi.iso2.comun.dominio.EstadoCita;
import es.uclm.esi.iso2.comun.dominio.Recurso;
import es.uclm.esi.iso2.comun.dominio.TipoPrueba;
import es.uclm.esi.iso2.comun.protocolo.CodigoError;
import es.uclm.esi.iso2.comun.protocolo.ProtocoloCitas;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;

class MensajesYFormatosCitasTest {

    @Test
    void etiquetasDeLosCampos() {
        assertEquals("Número de cita", MensajesCitas.etiqueta(ProtocoloCitas.CAMPO_ID));
        assertEquals("Tipo de prueba", MensajesCitas.etiqueta(ProtocoloCitas.CAMPO_TIPO_PRUEBA));
        assertEquals("Recurso", MensajesCitas.etiqueta(ProtocoloCitas.CAMPO_RECURSO));
        assertEquals("Fecha y hora", MensajesCitas.etiqueta(ProtocoloCitas.CAMPO_INICIO));
        assertEquals("Desde", MensajesCitas.etiqueta(ProtocoloCitas.CAMPO_DESDE));
        assertEquals("Estado", MensajesCitas.etiqueta(ProtocoloCitas.CAMPO_ESTADO));
        assertEquals("otro", MensajesCitas.etiqueta("otro"));
    }

    @Test
    void formatosEsperados() {
        assertTrue(MensajesCitas.formatoEsperado(ProtocoloCitas.CAMPO_DNI).contains("8 dígitos"));
        assertEquals("uno de los de la lista", MensajesCitas.formatoEsperado(ProtocoloCitas.CAMPO_RECURSO));
        assertEquals("uno de los de la lista", MensajesCitas.formatoEsperado(ProtocoloCitas.CAMPO_TIPO_PRUEBA));
        assertEquals("una fecha dd/mm/aaaa", MensajesCitas.formatoEsperado(ProtocoloCitas.CAMPO_DESDE));
        assertEquals("un valor válido", MensajesCitas.formatoEsperado(ProtocoloCitas.CAMPO_ESTADO));
    }

    @Test
    void mensajesDeError() {
        assertEquals("Falta un campo obligatorio.", MensajesCitas.mensaje(CodigoError.CAMPO_OBLIGATORIO, null));
        assertEquals("Hay un campo con un formato no válido.", MensajesCitas.mensaje(CodigoError.CAMPO_NO_VALIDO, null));
        assertEquals("El campo «Tipo de prueba» no es válido: debe ser uno de los de la lista.",
                MensajesCitas.mensaje(CodigoError.CAMPO_NO_VALIDO, ProtocoloCitas.CAMPO_TIPO_PRUEBA));
        assertEquals("El recurso ya tiene otra cita a esa hora.", MensajesCitas.mensaje(CodigoError.SOLAPE_RECURSO, null));
        assertEquals("El paciente ya tiene otra cita a esa hora.", MensajesCitas.mensaje(CodigoError.SOLAPE_PACIENTE, " "));
        assertEquals("No existe esa cita.", MensajesCitas.mensaje(CodigoError.CITA_NO_ENCONTRADA, null));
        assertTrue(MensajesCitas.mensaje(CodigoError.ERROR_INTERNO, null).startsWith("Se ha producido un error"));
    }

    @Test
    void fechasHorasYEstados() {
        assertEquals("30/11/2026 09:05", FormatosCitas.fechaHora(LocalDateTime.of(2026, 11, 30, 9, 5)));
        assertEquals("", FormatosCitas.fechaHora(null));
        assertEquals("", FormatosCitas.fecha(null));
        assertEquals("", FormatosCitas.hora(null));
        assertEquals("01/12/2026", FormatosCitas.fecha(LocalDate.of(2026, 12, 1)));
        assertEquals("20:00", FormatosCitas.hora(LocalTime.of(20, 0)));
        assertEquals("Programada", FormatosCitas.estado(EstadoCita.PROGRAMADA));
        assertEquals("Cancelada", FormatosCitas.estado(EstadoCita.CANCELADA));
        assertEquals("Realizada", FormatosCitas.estado(EstadoCita.REALIZADA));
    }

    @Test
    void nombresDelCatalogo() {
        Catalogo catalogo = new Catalogo(List.of(new TipoPrueba(1, "Mamografía", 20)),
                List.of(new Recurso(3, "Mamógrafo 1")));
        assertEquals("Mamografía", catalogo.nombreTipo(1));
        assertEquals("Prueba nº 9", catalogo.nombreTipo(9));
        assertEquals("Mamógrafo 1", catalogo.nombreRecurso(3));
        assertEquals("Recurso nº 9", catalogo.nombreRecurso(9));
    }
}
