package es.uclm.esi.iso2.cliente.logica;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import es.uclm.esi.iso2.comun.dominio.CampoPaciente;
import es.uclm.esi.iso2.comun.dominio.Paciente;
import es.uclm.esi.iso2.comun.protocolo.CodigoError;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class MensajesYFormatosTest {

    // ---- MensajesError ----

    @ParameterizedTest
    @EnumSource(CodigoError.class)
    void todosLosCodigosTienenMensaje(CodigoError codigo) {
        assertFalse(MensajesError.mensaje(codigo, null).isBlank());
        assertFalse(MensajesError.mensaje(codigo, CampoPaciente.DNI).isBlank());
    }

    @ParameterizedTest
    @EnumSource(CampoPaciente.class)
    void todosLosCamposTienenEtiquetaYFormato(CampoPaciente campo) {
        assertFalse(MensajesError.etiqueta(campo).isBlank());
        assertFalse(MensajesError.formatoEsperado(campo).isBlank());
        assertTrue(MensajesError.mensaje(CodigoError.CAMPO_NO_VALIDO, campo).contains(MensajesError.etiqueta(campo)));
    }

    @Test
    void mensajesDeCampo() {
        assertEquals("Falta el campo obligatorio «Teléfono».",
                MensajesError.mensaje(CodigoError.CAMPO_OBLIGATORIO, CampoPaciente.TELEFONO));
        assertEquals("Falta un campo obligatorio.", MensajesError.mensaje(CodigoError.CAMPO_OBLIGATORIO, null));
        assertEquals("Hay un campo con un formato no válido.", MensajesError.mensaje(CodigoError.CAMPO_NO_VALIDO, null));
    }

    @Test
    void campoAfectado() {
        assertEquals(CampoPaciente.NOMBRE, MensajesError.campoAfectado(CodigoError.CAMPO_NO_VALIDO, CampoPaciente.NOMBRE));
        assertEquals(CampoPaciente.DNI, MensajesError.campoAfectado(CodigoError.DNI_DUPLICADO, null));
        assertEquals(CampoPaciente.TARJETA_SANITARIA, MensajesError.campoAfectado(CodigoError.TARJETA_DUPLICADA, null));
        assertNull(MensajesError.campoAfectado(CodigoError.ERROR_INTERNO, null));
    }

    // ---- Formatos ----

    @Test
    void fechaDePantallaAIso() {
        assertEquals("1985-04-12", Formatos.fechaAIso(" 12/04/1985 "));
        assertEquals("", Formatos.fechaAIso(""));
        assertEquals("", Formatos.fechaAIso(null));
        // Una fecha que no existe se deja tal cual para que la validación la rechace
        assertEquals("31/02/2000", Formatos.fechaAIso("31/02/2000"));
        assertEquals("ayer", Formatos.fechaAIso("ayer"));
    }

    @Test
    void fechaIsoAPantalla() {
        assertEquals("12/04/1985", Formatos.fechaAPantalla("1985-04-12"));
        assertEquals("", Formatos.fechaAPantalla(""));
        assertEquals("", Formatos.fechaAPantalla(null));
        assertEquals("no-es-fecha", Formatos.fechaAPantalla("no-es-fecha"));
    }

    @Test
    void sexo() {
        assertEquals(Paciente.HOMBRE, Formatos.sexoAProtocolo(Formatos.SEXO_HOMBRE));
        assertEquals(Paciente.MUJER, Formatos.sexoAProtocolo(Formatos.SEXO_MUJER));
        assertEquals("", Formatos.sexoAProtocolo(Formatos.SEXO_SIN_INDICAR));
        assertEquals("", Formatos.sexoAProtocolo(null));
        assertEquals(Formatos.SEXO_HOMBRE, Formatos.sexoAPantalla(Paciente.HOMBRE));
        assertEquals(Formatos.SEXO_MUJER, Formatos.sexoAPantalla(Paciente.MUJER));
        assertEquals(Formatos.SEXO_SIN_INDICAR, Formatos.sexoAPantalla(""));
    }

    @Test
    void guionParaVacios() {
        assertEquals("—", Formatos.oGuion(""));
        assertEquals("—", Formatos.oGuion(null));
        assertEquals("—", Formatos.oGuion("  "));
        assertEquals("x", Formatos.oGuion("x"));
    }
}
