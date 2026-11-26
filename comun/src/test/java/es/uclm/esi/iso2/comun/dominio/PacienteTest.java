package es.uclm.esi.iso2.comun.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PacienteTest {

    @Test
    void losNulosSeConviertenEnCadenasVaciasYElEstadoEnActivo() {
        Paciente p = new Paciente(null, null, null, null, null, null, null, null, null, null);
        for (CampoPaciente campo : CampoPaciente.values()) {
            assertEquals("", p.valor(campo), campo.name());
        }
        assertEquals(EstadoPaciente.ACTIVO, p.estado());
        assertTrue(p.activo());
    }

    @Test
    void normalizadoQuitaEspaciosYPasaAMayusculas() {
        Paciente p = new Paciente(" 12345678z ", " cmcm000000000001", " Lucía ", " García ", " 1985-04-12 ",
                " mujer ", " 600111222 ", " a@b.es ", " C/ Mayor ", EstadoPaciente.BAJA).normalizado();
        assertEquals(new Paciente("12345678Z", "CMCM000000000001", "Lucía", "García", "1985-04-12", "MUJER",
                "600111222", "a@b.es", "C/ Mayor", EstadoPaciente.BAJA), p);
    }

    @Test
    void valorDevuelveCadaCampo() {
        Paciente p = new Paciente("1", "2", "3", "4", "5", "6", "7", "8", "9", null);
        CampoPaciente[] campos = CampoPaciente.values();
        for (int i = 0; i < campos.length; i++) {
            assertEquals(String.valueOf(i + 1), p.valor(campos[i]));
        }
    }

    @Test
    void conEstadoYNombreCompleto() {
        Paciente p = new Paciente("12345678Z", "", "Ana", "Ruiz", "", "", "", "", "", null);
        Paciente deBaja = p.conEstado(EstadoPaciente.BAJA);
        assertFalse(deBaja.activo());
        assertEquals(p.dni(), deBaja.dni());
        assertEquals("Ana Ruiz", p.nombreCompleto());
        assertEquals("Ana", new Paciente("", "", "Ana", "", "", "", "", "", "", null).nombreCompleto());
    }

    @Test
    void normalizarDni() {
        assertEquals("X1234567L", Paciente.normalizarDni(" x1234567l "));
        assertEquals("", Paciente.normalizarDni(null));
    }

    @Test
    void campoDesdeNombreDeProtocolo() {
        for (CampoPaciente campo : CampoPaciente.values()) {
            assertEquals(campo, CampoPaciente.desdeNombreProtocolo(campo.nombreProtocolo()));
        }
        assertEquals("tarjetaSanitaria", CampoPaciente.TARJETA_SANITARIA.nombreProtocolo());
        assertThrows(IllegalArgumentException.class, () -> CampoPaciente.desdeNombreProtocolo("edad"));
    }
}
