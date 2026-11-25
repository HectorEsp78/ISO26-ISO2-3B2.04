package es.uclm.esi.iso2.comun.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import es.uclm.esi.iso2.comun.protocolo.CodigoError;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Clases de equivalencia y valores límite de los datos del paciente (DEC-055).
 */
class ValidadorPacienteTest {

    private static final LocalDate HOY = LocalDate.of(2026, 11, 25);
    private final ValidadorPaciente validador = new ValidadorPaciente(
            Clock.fixed(HOY.atStartOfDay().toInstant(ZoneOffset.UTC), ZoneId.of("UTC")));

    static Paciente valido() {
        return new Paciente("12345678Z", "CMCM000000000001", "Lucía", "García Martín", "1985-04-12",
                Paciente.MUJER, "600111222", "lucia@example.com", "C/ Mayor 1; 2º B", null);
    }

    private static Paciente con(CampoPaciente campo, String valor) {
        Paciente p = valido();
        return new Paciente(
                campo == CampoPaciente.DNI ? valor : p.dni(),
                campo == CampoPaciente.TARJETA_SANITARIA ? valor : p.tarjetaSanitaria(),
                campo == CampoPaciente.NOMBRE ? valor : p.nombre(),
                campo == CampoPaciente.APELLIDOS ? valor : p.apellidos(),
                campo == CampoPaciente.FECHA_NACIMIENTO ? valor : p.fechaNacimiento(),
                campo == CampoPaciente.SEXO ? valor : p.sexo(),
                campo == CampoPaciente.TELEFONO ? valor : p.telefono(),
                campo == CampoPaciente.CORREO ? valor : p.correo(),
                campo == CampoPaciente.DIRECCION ? valor : p.direccion(),
                p.estado());
    }

    private void assertValido(CampoPaciente campo, String valor) {
        assertEquals(List.of(), validador.validar(con(campo, valor)), campo + " = '" + valor + "'");
    }

    private void assertError(CampoPaciente campo, String valor, CodigoError codigo) {
        assertEquals(List.of(new ErrorValidacion(campo, codigo)), validador.validar(con(campo, valor)),
                campo + " = '" + valor + "'");
    }

    @Test
    void pacienteValidoNoTieneErrores() {
        assertEquals(List.of(), validador.validar(valido()));
        assertEquals(Optional.empty(), validador.primerError(valido()));
    }

    @Test
    void elConstructorSinRelojUsaLaFechaActual() {
        assertTrue(new ValidadorPaciente().validar(valido()).isEmpty());
    }

    // ---- Obligatorios ----

    @ParameterizedTest
    @ValueSource(strings = {"DNI", "TARJETA_SANITARIA", "NOMBRE", "APELLIDOS", "TELEFONO"})
    void camposObligatoriosVacios(String nombre) {
        CampoPaciente campo = CampoPaciente.valueOf(nombre);
        assertError(campo, "", CodigoError.CAMPO_OBLIGATORIO);
        assertError(campo, "   ", CodigoError.CAMPO_OBLIGATORIO);
    }

    @ParameterizedTest
    @ValueSource(strings = {"FECHA_NACIMIENTO", "SEXO", "CORREO", "DIRECCION"})
    void camposOpcionalesVacios(String nombre) {
        assertValido(CampoPaciente.valueOf(nombre), "");
    }

    // ---- DNI / NIE ----

    @ParameterizedTest
    @ValueSource(strings = {"12345678Z", "00000000T", "12345678z", " 12345678Z ", "X1234567L", "Y1234567X",
        "Z1234567R"})
    void dniYNieValidos(String dni) {
        assertValido(CampoPaciente.DNI, dni);
    }

    @ParameterizedTest
    @ValueSource(strings = {"12345678A", "1234567Z", "123456789Z", "12345678", "ABCDEFGHZ", "X1234567A",
        "W1234567L", "X12345678L", "12.345.678-Z"})
    void dniYNieNoValidos(String dni) {
        assertError(CampoPaciente.DNI, dni, CodigoError.CAMPO_NO_VALIDO);
    }

    @Test
    void validarDniSolo() {
        assertEquals(Optional.empty(), validador.validarDni("12345678Z"));
        assertEquals(Optional.of(new ErrorValidacion(CampoPaciente.DNI, CodigoError.CAMPO_OBLIGATORIO)),
                validador.validarDni(null));
        assertEquals(Optional.of(new ErrorValidacion(CampoPaciente.DNI, CodigoError.CAMPO_NO_VALIDO)),
                validador.validarDni("123"));
    }

    @Test
    void dniValidoEstatico() {
        assertTrue(ValidadorPaciente.dniValido("12345678Z"));
        assertFalse(ValidadorPaciente.dniValido("12345678z"));
    }

    // ---- Tarjeta sanitaria ----

    @ParameterizedTest
    @ValueSource(strings = {"CMCM000000000001", "cmcm000000000001", "1234567890123456"})
    void tarjetasValidas(String tarjeta) {
        assertValido(CampoPaciente.TARJETA_SANITARIA, tarjeta);
    }

    @ParameterizedTest
    @ValueSource(strings = {"CMCM00000000001", "CMCM0000000000012", "CMCM-00000000001", "CMCM 00000000001"})
    void tarjetasNoValidas(String tarjeta) {
        assertError(CampoPaciente.TARJETA_SANITARIA, tarjeta, CodigoError.CAMPO_NO_VALIDO);
    }

    // ---- Nombre y apellidos ----

    @Test
    void longitudMaximaDelNombre() {
        assertValido(CampoPaciente.NOMBRE, "a".repeat(ValidadorPaciente.MAX_NOMBRE));
        assertError(CampoPaciente.NOMBRE, "a".repeat(ValidadorPaciente.MAX_NOMBRE + 1), CodigoError.CAMPO_NO_VALIDO);
    }

    @Test
    void longitudMaximaDeLosApellidos() {
        assertValido(CampoPaciente.APELLIDOS, "a".repeat(ValidadorPaciente.MAX_APELLIDOS));
        assertError(CampoPaciente.APELLIDOS, "a".repeat(ValidadorPaciente.MAX_APELLIDOS + 1),
                CodigoError.CAMPO_NO_VALIDO);
    }

    @ParameterizedTest
    @ValueSource(strings = {"María José", "Jean-Luc", "D'Angelo", "Íñigo", "Mª"})
    void nombresValidos(String nombre) {
        assertValido(CampoPaciente.NOMBRE, nombre);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Juan2", "Ana;", "-", "' -", "Pedro_"})
    void nombresNoValidos(String nombre) {
        assertError(CampoPaciente.NOMBRE, nombre, CodigoError.CAMPO_NO_VALIDO);
    }

    // ---- Fecha de nacimiento ----

    @Test
    void fechaDeNacimientoEnLosLimites() {
        assertValido(CampoPaciente.FECHA_NACIMIENTO, HOY.toString());
        assertValido(CampoPaciente.FECHA_NACIMIENTO, "1900-01-01");
        assertError(CampoPaciente.FECHA_NACIMIENTO, HOY.plusDays(1).toString(), CodigoError.CAMPO_NO_VALIDO);
        assertError(CampoPaciente.FECHA_NACIMIENTO, "1899-12-31", CodigoError.CAMPO_NO_VALIDO);
    }

    @ParameterizedTest
    @ValueSource(strings = {"2000-02-30", "12/04/1985", "ayer", "2000-1-1"})
    void fechasConFormatoNoValido(String fecha) {
        assertError(CampoPaciente.FECHA_NACIMIENTO, fecha, CodigoError.CAMPO_NO_VALIDO);
    }

    // ---- Sexo ----

    @ParameterizedTest
    @ValueSource(strings = {"HOMBRE", "MUJER", "mujer"})
    void sexosValidos(String sexo) {
        assertValido(CampoPaciente.SEXO, sexo);
    }

    @Test
    void sexoNoValido() {
        assertError(CampoPaciente.SEXO, "OTRO", CodigoError.CAMPO_NO_VALIDO);
    }

    // ---- Teléfono ----

    @ParameterizedTest
    @ValueSource(strings = {"600000000", "700000000", "800000000", "926000000"})
    void telefonosValidos(String telefono) {
        assertValido(CampoPaciente.TELEFONO, telefono);
    }

    @ParameterizedTest
    @ValueSource(strings = {"500000000", "60000000", "6000000000", "60000000A", "+34600000000"})
    void telefonosNoValidos(String telefono) {
        assertError(CampoPaciente.TELEFONO, telefono, CodigoError.CAMPO_NO_VALIDO);
    }

    // ---- Correo ----

    @ParameterizedTest
    @ValueSource(strings = {"a@b.es", "nombre.apellido+cita@sescam.jccm.es"})
    void correosValidos(String correo) {
        assertValido(CampoPaciente.CORREO, correo);
    }

    @ParameterizedTest
    @ValueSource(strings = {"sin-arroba.es", "a@b", "a@.es", "a b@c.es", "a@b.c"})
    void correosNoValidos(String correo) {
        assertError(CampoPaciente.CORREO, correo, CodigoError.CAMPO_NO_VALIDO);
    }

    @Test
    void longitudMaximaDelCorreo() {
        String dominio = "@ejemplo.es";
        assertValido(CampoPaciente.CORREO, "a".repeat(ValidadorPaciente.MAX_CORREO - dominio.length()) + dominio);
        assertError(CampoPaciente.CORREO, "a".repeat(ValidadorPaciente.MAX_CORREO - dominio.length() + 1) + dominio,
                CodigoError.CAMPO_NO_VALIDO);
    }

    // ---- Dirección ----

    @Test
    void longitudMaximaDeLaDireccion() {
        assertValido(CampoPaciente.DIRECCION, "a".repeat(ValidadorPaciente.MAX_DIRECCION));
        assertError(CampoPaciente.DIRECCION, "a".repeat(ValidadorPaciente.MAX_DIRECCION + 1),
                CodigoError.CAMPO_NO_VALIDO);
    }

    // ---- Varios errores ----

    @Test
    void variosErroresEnElOrdenDeLosCampos() {
        Paciente p = new Paciente("", "corta", "Ana", "", "", "", "123", "", "", null);
        List<ErrorValidacion> errores = validador.validar(p);
        assertEquals(List.of(
                new ErrorValidacion(CampoPaciente.DNI, CodigoError.CAMPO_OBLIGATORIO),
                new ErrorValidacion(CampoPaciente.TARJETA_SANITARIA, CodigoError.CAMPO_NO_VALIDO),
                new ErrorValidacion(CampoPaciente.APELLIDOS, CodigoError.CAMPO_OBLIGATORIO),
                new ErrorValidacion(CampoPaciente.TELEFONO, CodigoError.CAMPO_NO_VALIDO)), errores);
        assertEquals(Optional.of(errores.get(0)), validador.primerError(p));
    }
}
