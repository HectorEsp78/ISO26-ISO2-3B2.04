package es.uclm.esi.iso2.comun.protocolo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import es.uclm.esi.iso2.comun.dominio.CampoPaciente;
import es.uclm.esi.iso2.comun.dominio.EstadoPaciente;
import es.uclm.esi.iso2.comun.dominio.Paciente;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ProtocoloTest {

    private static final Paciente PACIENTE = new Paciente("12345678Z", "CMCM000000000001", "Lucía",
            "García Martín", "1985-04-12", "MUJER", "600111222", "", "C/ Mayor 1; 2º B", EstadoPaciente.BAJA);

    @Test
    void escapaLosCaracteresEspeciales() {
        assertEquals("a\\;b\\\\c\\nd\\re", Protocolo.escapar("a;b\\c\nd\re"));
        assertEquals("normal", Protocolo.escapar("normal"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "texto", "a;b", "\\", "\\;", ";;", "línea1\nlínea2\r", "fin\\"})
    void componerYDescomponerSonInversas(String campo) {
        List<String> campos = List.of("OP", campo, "", campo);
        assertEquals(campos, Protocolo.descomponer(Protocolo.componer(campos)));
    }

    @Test
    void componerTrataNuloComoVacio() {
        List<String> campos = new ArrayList<>();
        campos.add("A");
        campos.add(null);
        assertEquals("A;", Protocolo.componer(campos));
        assertEquals("A;B", Protocolo.componer("A", "B"));
    }

    @Test
    void descomponerCasosBasicos() {
        assertEquals(List.of(""), Protocolo.descomponer(""));
        assertEquals(List.of("OK", "PONG"), Protocolo.descomponer("OK;PONG"));
        assertEquals(List.of("A", "", ""), Protocolo.descomponer("A;;"));
    }

    @Test
    void descomponerRechazaEscapesNoValidos() {
        assertThrows(IllegalArgumentException.class, () -> Protocolo.descomponer("A;fin\\"));
        assertThrows(IllegalArgumentException.class, () -> Protocolo.descomponer("A\\x"));
    }

    @Test
    void camposDelPaciente() {
        List<String> datos = Protocolo.camposDatos(PACIENTE);
        assertEquals(Protocolo.CAMPOS_DATOS, datos.size());
        assertEquals(List.of("12345678Z", "CMCM000000000001", "Lucía", "García Martín", "1985-04-12", "MUJER",
                "600111222", "", "C/ Mayor 1; 2º B"), datos);
        List<String> completos = Protocolo.camposPaciente(PACIENTE);
        assertEquals(Protocolo.CAMPOS_PACIENTE, completos.size());
        assertEquals("BAJA", completos.get(9));
    }

    @Test
    void pacienteDesdeDatosYDesdeCampos() {
        List<String> conPrefijo = new ArrayList<>(List.of("OP"));
        conPrefijo.addAll(Protocolo.camposDatos(PACIENTE));
        assertEquals(PACIENTE.conEstado(EstadoPaciente.ACTIVO), Protocolo.pacienteDesdeDatos(conPrefijo, 1));
        assertEquals(PACIENTE, Protocolo.pacienteDesdeCampos(Protocolo.camposPaciente(PACIENTE), 0));
    }

    @Test
    void pacienteConNumeroDeCamposIncorrecto() {
        List<String> datos = Protocolo.camposDatos(PACIENTE);
        assertThrows(IllegalArgumentException.class, () -> Protocolo.pacienteDesdeDatos(datos, 1));
        assertThrows(IllegalArgumentException.class, () -> Protocolo.pacienteDesdeCampos(datos, 0));
    }

    @Test
    void pacienteConEstadoDesconocido() {
        List<String> campos = Protocolo.camposDatos(PACIENTE);
        campos.add("JUBILADO");
        assertThrows(IllegalArgumentException.class, () -> Protocolo.pacienteDesdeCampos(campos, 0));
    }

    @Test
    void peticiones() {
        String datos = "12345678Z;CMCM000000000001;Lucía;García Martín;1985-04-12;MUJER;600111222;;C/ Mayor 1\\; 2º B";
        assertEquals("ALTA_PACIENTE;" + datos, Protocolo.peticionAlta(PACIENTE));
        assertEquals("MODIFICAR_PACIENTE;" + datos, Protocolo.peticionModificar(PACIENTE));
        assertEquals("BUSCAR_PACIENTE;12345678Z", Protocolo.peticionBuscar("12345678Z"));
        assertEquals("BAJA_PACIENTE;12345678Z", Protocolo.peticionBaja("12345678Z"));
    }

    @Test
    void comprobarPaciente() {
        assertEquals("COMPROBAR_PACIENTE;X1234567L", Protocolo.peticionComprobar("X1234567L"));
        assertEquals("OK;ACTIVO", Protocolo.respuestaOk(EstadoPaciente.ACTIVO));
        assertEquals("OK;BAJA", Protocolo.respuestaOk(EstadoPaciente.BAJA));
        assertEquals(EstadoPaciente.BAJA, Protocolo.estadoDesdeCampos(List.of("BAJA")));
        assertThrows(IllegalArgumentException.class, () -> Protocolo.estadoDesdeCampos(List.of()));
        assertThrows(IllegalArgumentException.class, () -> Protocolo.estadoDesdeCampos(List.of("ACTIVO", "x")));
        assertThrows(IllegalArgumentException.class, () -> Protocolo.estadoDesdeCampos(List.of("ACTIVA")));
    }

    @Test
    void respuestas() {
        assertEquals("OK;12345678Z;CMCM000000000001;Lucía;García Martín;1985-04-12;MUJER;600111222;;"
                + "C/ Mayor 1\\; 2º B;BAJA", Protocolo.respuestaOk(PACIENTE));
        assertEquals("ERROR;PACIENTE_NO_ENCONTRADO", Protocolo.respuestaError(CodigoError.PACIENTE_NO_ENCONTRADO, null));
        assertEquals("ERROR;CAMPO_OBLIGATORIO;telefono",
                Protocolo.respuestaError(CodigoError.CAMPO_OBLIGATORIO, CampoPaciente.TELEFONO));
        assertEquals("ERROR;PETICION_NO_VALIDA", Protocolo.respuestaErrorConDetalle(CodigoError.PETICION_NO_VALIDA, null));
        assertEquals("ERROR;CAMPO_NO_VALIDO;hallazgos",
                Protocolo.respuestaErrorConDetalle(CodigoError.CAMPO_NO_VALIDO, "hallazgos"));
        assertEquals("ERROR;CAMPO_NO_VALIDO;a\\;b",
                Protocolo.respuestaErrorConDetalle(CodigoError.CAMPO_NO_VALIDO, "a;b"));
    }
}
