package es.uclm.esi.iso2.comun.protocolo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import es.uclm.esi.iso2.comun.dominio.CampoPaciente;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class RespuestaTest {

    @Test
    void respuestaCorrectaConDatos() {
        Respuesta r = Respuesta.leer("OK;a\\;b;c");
        assertTrue(r.correcta());
        assertEquals(List.of("a;b", "c"), r.datos());
        assertNull(r.codigo());
        assertNull(r.campo());
    }

    @Test
    void respuestaCorrectaSinDatos() {
        assertEquals(List.of(), Respuesta.leer("OK").datos());
    }

    @Test
    void errorSinCampo() {
        Respuesta r = Respuesta.leer("ERROR;DNI_DUPLICADO");
        assertFalse(r.correcta());
        assertEquals(CodigoError.DNI_DUPLICADO, r.codigo());
        assertNull(r.campo());
        assertNull(r.detalle());
        assertEquals(List.of(), r.datos());
    }

    @Test
    void errorConCampo() {
        Respuesta r = Respuesta.leer("ERROR;CAMPO_NO_VALIDO;fechaNacimiento");
        assertEquals(CodigoError.CAMPO_NO_VALIDO, r.codigo());
        assertEquals(CampoPaciente.FECHA_NACIMIENTO, r.campo());
        assertEquals("fechaNacimiento", r.detalle());
    }

    @Test
    void errorConDetalleQueNoEsUnCampoDePaciente() {
        Respuesta r = Respuesta.leer("ERROR;CAMPO_NO_VALIDO;hallazgos");
        assertFalse(r.correcta());
        assertEquals(CodigoError.CAMPO_NO_VALIDO, r.codigo());
        assertNull(r.campo());
        assertEquals("hallazgos", r.detalle());
    }

    @Test
    void errorConDetalleEscapado() {
        Respuesta r = Respuesta.leer("ERROR;CAMPO_NO_VALIDO;a\\;b");
        assertNull(r.campo());
        assertEquals("a;b", r.detalle());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "PONG", "ERROR", "ERROR;DNI_DUPLICADO;dni;otro", "ERROR;NO_EXISTE",
        "OK\\x"})
    void respuestasNoValidas(String linea) {
        assertThrows(IllegalArgumentException.class, () -> Respuesta.leer(linea));
    }
}
